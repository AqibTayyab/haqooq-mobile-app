package com.example.services.ai

import android.util.Log
import com.example.contracts.AiMode
import com.example.contracts.AiService
import com.example.contracts.AppLanguage
import com.example.contracts.AskRequest
import com.example.contracts.AskResponse
import com.example.contracts.BriefingTimelineEvent
import com.example.contracts.ChatMessage
import com.example.contracts.LawyerBriefingPack
import com.example.contracts.LegalCard
import com.example.contracts.LegalCategory
import com.example.contracts.RagService
import com.example.contracts.SourceRef
import com.example.contracts.UrgencyLevel
import com.example.l10n.AppStrings
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "HybridAiService"
private const val MODEL_NAME = "gemini-3.5-flash-lite"
private const val AI_TIMEOUT_MS = 45_000L

/**
 * Hybrid AI service for HAQOOQ:
 * - Online mode with internet: calls Gemini via Firebase AI Logic with structured JSON output.
 * - Offline / Library-only mode, or when no internet is available: uses grounded local RAG + card engine.
 */
class HybridAiService(
    private val ragService: RagService,
    private val offlineFallback: AiService,
    private val isNetworkAvailable: () -> Boolean,
    private val currentModeProvider: () -> AiMode
) : AiService {

    private val unsafePatterns = listOf(
        "hide evidence", "destroy evidence", "fake evidence", "lie to police",
        "bribe", "evade arrest", "ignore rules", "ignore previous",
        "ثبوت مٹا", "جھوٹ بول", "saboot mita", "jhoot"
    )

    private val askResponseSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "answer" to Schema.string("Clear, plain-language legal information grounded in the provided cards and Pakistani civic context, written in the requested language."),
                "legalCategoryId" to Schema.enumeration(
                    values = LegalCategory.entries.filter { it != LegalCategory.ALL }.map { it.id },
                    description = "Best matching legal category id"
                ),
                "urgency" to Schema.enumeration(
                    values = listOf("LOW", "MEDIUM", "HIGH"),
                    description = "Urgency level of the situation"
                ),
                "nextSteps" to Schema.array(
                    items = Schema.string(),
                    description = "2 to 4 concrete, lawful next steps in the requested language"
                ),
                "followUpQuestions" to Schema.array(
                    items = Schema.string(),
                    description = "Up to 2 helpful clarifying follow-up questions in the requested language"
                ),
                "shouldContactLawyer" to Schema.boolean("True if consulting a licensed advocate is recommended"),
                "isUrgentEmergency" to Schema.boolean("True if the user describes immediate physical danger, active arrest, or ongoing violence"),
                "citedCardIds" to Schema.array(
                    items = Schema.string(),
                    description = "Subset of provided legal card IDs referenced in the response"
                )
            )
        )
    }

    private val briefingSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "summary" to Schema.string("Concise case summary for an advocate in the requested language"),
                "timeline" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "time" to Schema.string("Timestamp or step label"),
                            "event" to Schema.string("Short description of the event")
                        )
                    ),
                    description = "Chronological timeline of key events"
                ),
                "peopleInvolved" to Schema.array(
                    items = Schema.string(),
                    description = "Parties or roles mentioned, marked unknown if unspecified"
                ),
                "evidenceChecklist" to Schema.array(
                    items = Schema.string(),
                    description = "Practical documents or proof the citizen should preserve"
                ),
                "questionsForLawyer" to Schema.array(
                    items = Schema.string(),
                    description = "3 key legal questions for the consultation"
                ),
                "urgency" to Schema.enumeration(
                    values = listOf("LOW", "MEDIUM", "HIGH"),
                    description = "Overall case urgency"
                )
            )
        )
    }

    private val askModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                temperature = 0.25f
                maxOutputTokens = 700
                responseMimeType = "application/json"
                responseSchema = askResponseSchema
            },
            systemInstruction = content {
                text(
                    "You are HAQOOQ, a concise citizen legal-information assistant for Pakistan. " +
                        "Provide clear general legal information (never formal legal advice or guaranteed court outcomes). " +
                        "Never help hide, destroy, or fabricate evidence, bribe officials, or evade lawful investigation. " +
                        "Do not invent specific statutory section numbers or punishment years unless present in the provided cards. " +
                        "Always respond concisely in the requested language (English, Urdu script, or Roman Urdu)."
                )
            }
        )
    }

    private val briefingModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                temperature = 0.2f
                maxOutputTokens = 800
                responseMimeType = "application/json"
                responseSchema = briefingSchema
            },
            systemInstruction = content {
                text(
                    "You organize citizen legal chat messages into a concise structured Lawyer Briefing Pack. " +
                        "Never invent facts not present in the conversation. Mark missing details as unknown. " +
                        "Write all fields strictly in the requested language."
                )
            }
        )
    }

    override suspend fun ask(request: AskRequest, mode: AiMode): AskResponse {
        val qLower = request.question.lowercase()
        if (unsafePatterns.any { qLower.contains(it) }) {
            return offlineFallback.ask(request, mode)
        }

        // Offline / Library-Only modes always run locally without network.
        if (mode != AiMode.ONLINE || !isNetworkAvailable()) {
            val effectiveOfflineMode = if (mode == AiMode.LIBRARY_ONLY) AiMode.LIBRARY_ONLY else AiMode.OFFLINE
            return offlineFallback.ask(request, effectiveOfflineMode)
        }

        val strings = AppStrings.forLanguage(request.language)
        val matchedCards = request.cards.ifEmpty {
            ragService.retrieveForAi(request.question, request.language, topK = 3)
        }
        val prompt = buildAskPrompt(request, matchedCards)

        return try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                askModel.generateContent(prompt)
            }
            val jsonString = response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Gemini returned an empty response.")

            parseAskJson(
                jsonString = jsonString,
                matchedCards = matchedCards,
                fallbackCategory = ragService.detectCategory(request.question),
                isUrgentHeuristic = ragService.isUrgentEmergencyQuery(request.question),
                disclaimer = strings.legalDisclaimer
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini online request timed out", e)
            throw IllegalStateException("Online AI timed out after 60 seconds. Try again or answer offline.", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Log.w(TAG, "Network unavailable during online AI request; falling back to offline RAG", e)
            offlineFallback.ask(request, AiMode.OFFLINE)
        } catch (e: Exception) {
            if (!isNetworkAvailable()) {
                Log.w(TAG, "Connection dropped during online AI call; using offline fallback", e)
                return offlineFallback.ask(request, AiMode.OFFLINE)
            }
            Log.e(TAG, "Firebase AI Logic request failed", e)
            throw e
        }
    }

    override suspend fun generateBriefing(
        language: AppLanguage,
        conversation: List<ChatMessage>
    ): LawyerBriefingPack {
        val mode = currentModeProvider()
        if (mode != AiMode.ONLINE || !isNetworkAvailable() || conversation.isEmpty()) {
            return offlineFallback.generateBriefing(language, conversation)
        }

        val langLabel = when (language) {
            AppLanguage.EN -> "English"
            AppLanguage.UR -> "Urdu (اردو script)"
            AppLanguage.ROMAN_UR -> "Roman Urdu (Latin script)"
        }
        val transcript = conversation.joinToString("\n") { msg ->
            val role = if (msg.isUser) "Citizen" else "Assistant"
            "[${msg.timestampLabel.ifBlank { "Now" }}] $role: ${msg.text}"
        }
        val prompt = """
            Requested output language: $langLabel
            Conversation transcript:
            $transcript
        """.trimIndent()

        return try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                briefingModel.generateContent(prompt)
            }
            val jsonString = response.text?.takeIf { it.isNotBlank() }
                ?: return offlineFallback.generateBriefing(language, conversation)
            parseBriefingJson(jsonString)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Online briefing generation fell back to local builder", e)
            offlineFallback.generateBriefing(language, conversation)
        }
    }

    private fun buildAskPrompt(request: AskRequest, cards: List<LegalCard>): String {
        val langLabel = when (request.language) {
            AppLanguage.EN -> "English"
            AppLanguage.UR -> "Urdu (اردو script)"
            AppLanguage.ROMAN_UR -> "Roman Urdu (everyday Pakistani Urdu in English/Latin letters)"
        }
        val cardsContext = if (cards.isEmpty()) {
            "No directly matching local HAQOOQ cards were retrieved."
        } else {
            cards.joinToString("\n\n") { card ->
                """
                Card ID: ${card.id}
                Category: ${card.category.id}
                Title: ${card.title}
                Guidance: ${card.body}
                Steps: ${card.steps.joinToString(" | ")}
                Source: ${card.sourceName} (${card.sourceUrl})
                """.trimIndent()
            }
        }

        return """
            Requested language: $langLabel
            City: ${request.city}
            Citizen question: ${request.question}

            Verified HAQOOQ reference cards:
            $cardsContext
        """.trimIndent()
    }

    private fun parseAskJson(
        jsonString: String,
        matchedCards: List<LegalCard>,
        fallbackCategory: LegalCategory,
        isUrgentHeuristic: Boolean,
        disclaimer: String
    ): AskResponse {
        val json = JSONObject(jsonString)
        val answer = json.optString("answer").ifBlank {
            matchedCards.firstOrNull()?.body ?: disclaimer
        }
        val categoryId = json.optString("legalCategoryId", fallbackCategory.id)
        val category = LegalCategory.entries.firstOrNull { it.id == categoryId } ?: fallbackCategory
        val urgencyStr = json.optString("urgency", "MEDIUM").uppercase()
        val urgency = UrgencyLevel.entries.firstOrNull { it.name == urgencyStr } ?: UrgencyLevel.MEDIUM

        val nextStepsArr = json.optJSONArray("nextSteps")
        val nextSteps = buildList {
            if (nextStepsArr != null) {
                for (i in 0 until nextStepsArr.length()) {
                    val step = nextStepsArr.optString(i).trim()
                    if (step.isNotEmpty()) add(step)
                }
            }
        }

        val followUpsArr = json.optJSONArray("followUpQuestions")
        val followUps = buildList {
            if (followUpsArr != null) {
                for (i in 0 until followUpsArr.length()) {
                    val q = followUpsArr.optString(i).trim()
                    if (q.isNotEmpty()) add(q)
                }
            }
        }

        val citedArr = json.optJSONArray("citedCardIds")
        val citedIds = buildSet {
            if (citedArr != null) {
                for (i in 0 until citedArr.length()) {
                    add(citedArr.optString(i))
                }
            }
        }
        val citedCards = matchedCards.filter { it.id in citedIds }.ifEmpty { matchedCards }
        val sources = citedCards.map { SourceRef(cardId = it.id, title = it.title, url = it.sourceUrl) }

        val urgent = json.optBoolean("isUrgentEmergency", false) || isUrgentHeuristic
        val contactLawyer = json.optBoolean("shouldContactLawyer", urgent)

        return AskResponse(
            answer = answer,
            legalCategory = category,
            urgency = if (urgent) UrgencyLevel.HIGH else urgency,
            sources = sources,
            nextSteps = nextSteps,
            followUpQuestions = followUps,
            shouldContactLawyer = contactLawyer,
            disclaimer = disclaimer,
            modeUsed = AiMode.ONLINE,
            isUrgentEmergency = urgent
        )
    }

    private fun parseBriefingJson(jsonString: String): LawyerBriefingPack {
        val json = JSONObject(jsonString)
        val timelineArr = json.optJSONArray("timeline")
        val timeline = buildList {
            if (timelineArr != null) {
                for (i in 0 until timelineArr.length()) {
                    val item = timelineArr.optJSONObject(i) ?: continue
                    add(
                        BriefingTimelineEvent(
                            time = item.optString("time", "Step ${i + 1}"),
                            event = item.optString("event", "")
                        )
                    )
                }
            }
        }
        val people = json.optJSONArray("peopleInvolved")?.let { arr ->
            List(arr.length()) { arr.optString(it) }.filter { it.isNotBlank() }
        }.orEmpty()
        val evidence = json.optJSONArray("evidenceChecklist")?.let { arr ->
            List(arr.length()) { arr.optString(it) }.filter { it.isNotBlank() }
        }.orEmpty()
        val questions = json.optJSONArray("questionsForLawyer")?.let { arr ->
            List(arr.length()) { arr.optString(it) }.filter { it.isNotBlank() }
        }.orEmpty()
        val urgencyStr = json.optString("urgency", "MEDIUM").uppercase()
        val urgency = UrgencyLevel.entries.firstOrNull { it.name == urgencyStr } ?: UrgencyLevel.MEDIUM
        val now = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(Date())

        return LawyerBriefingPack(
            summary = json.getString("summary"),
            timeline = timeline,
            peopleInvolved = people,
            evidenceChecklist = evidence,
            questionsForLawyer = questions,
            urgency = urgency,
            createdAt = now
        )
    }
}
