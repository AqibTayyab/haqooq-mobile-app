package com.example.data

import com.example.contracts.AiMode
import com.example.contracts.AiService
import com.example.contracts.AppLanguage
import com.example.contracts.AskRequest
import com.example.contracts.AskResponse
import com.example.contracts.BriefingTimelineEvent
import com.example.contracts.ChatMessage
import com.example.contracts.ConnectivityService
import com.example.contracts.EmergencyContactItem
import com.example.contracts.Lawyer
import com.example.contracts.LawyerBooking
import com.example.contracts.LawyerBriefingPack
import com.example.contracts.LawyerReview
import com.example.contracts.LawyerService
import com.example.contracts.LegalCard
import com.example.contracts.LegalCategory
import com.example.contracts.RagService
import com.example.contracts.SosIncident
import com.example.contracts.SosService
import com.example.contracts.SourceRef
import com.example.contracts.TrustedContact
import com.example.contracts.UrgencyLevel
import com.example.l10n.AppStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MockConnectivityService : ConnectivityService {
    private val _activeMode = MutableStateFlow(AiMode.ONLINE)
    override val activeMode: StateFlow<AiMode> = _activeMode.asStateFlow()

    private var overrideMode: AiMode? = null
    private var simulatedOffline = false

    override fun setModeOverride(mode: AiMode?) {
        overrideMode = mode
        recompute()
    }

    override fun setSimulatedOffline(offline: Boolean) {
        simulatedOffline = offline
        recompute()
    }

    private fun recompute() {
        _activeMode.value = when {
            overrideMode != null -> overrideMode!!
            simulatedOffline -> AiMode.OFFLINE
            else -> AiMode.ONLINE
        }
    }
}

class MockRagService : RagService {
    private val synonymMap = mapOf(
        "giraftar" to listOf("arrest", "detain", "police", "thana"),
        "roka" to listOf("stop", "police", "checkpoint"),
        "thana" to listOf("police", "fir", "arrest"),
        "zamanat" to listOf("bail", "court"),
        "tankhwah" to listOf("salary", "wage", "employer"),
        "naukri" to listOf("employment", "job", "salary"),
        "kiraya" to listOf("tenant", "landlord", "rent"),
        "zameen" to listOf("property", "qabza"),
        "saboot" to listOf("evidence", "proof"),
        "پولیس" to listOf("police", "stop", "arrest"),
        "ایف" to listOf("fir"),
        "گرفتار" to listOf("arrest", "giraftar"),
        "ہراسانی" to listOf("harass", "women", "cyber"),
        "تنخواہ" to listOf("salary", "tankhwah")
    )

    private val urgentKeywords = listOf(
        "arrest now", "arrested now", "maar raha", "bachao", "kidnap", "beating",
        "violence", "threat", "dhamki", "gun", "emergency", "بچاؤ", "خطرہ", "مار رہا", "گرفتار کر لیا"
    )

    override suspend fun getAllCards(lang: AppLanguage): List<LegalCard> =
        LegalCardsData.cardsForLanguage(lang)

    override suspend fun searchCards(
        query: String,
        category: LegalCategory,
        lang: AppLanguage
    ): List<LegalCard> {
        val base = getAllCards(lang).filter {
            category == LegalCategory.ALL || it.category == category
        }
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return base

        return scoreCards(trimmed, base).map { it.first }
    }

    override suspend fun retrieveForAi(
        query: String,
        lang: AppLanguage,
        topK: Int
    ): List<LegalCard> {
        val candidates = getAllCards(lang)
        val scored = scoreCards(query, candidates)
        return scored.filter { it.second > 0 }.take(topK).map { it.first }
    }

    override fun detectCategory(query: String): LegalCategory {
        val q = query.lowercase()
        return when {
            listOf("fir", "report", "ایف آئی آر").any { q.contains(it) } -> LegalCategory.FIR
            listOf("bail", "zamanat", "ضمانت").any { q.contains(it) } -> LegalCategory.BAIL_COURT
            listOf("hack", "cyber", "blackmail", "online", "scam", "سائبر", "ہیک").any { q.contains(it) } -> LegalCategory.CYBERCRIME
            listOf("harass", "workplace", "women", "ہراسانی", "خواتین").any { q.contains(it) } -> LegalCategory.WOMEN_SAFETY
            listOf("domestic", "maar", "ghar", "husband", "تشدد").any { q.contains(it) } -> LegalCategory.DOMESTIC
            listOf("salary", "tankhwah", "employer", "naukri", "تنخواہ").any { q.contains(it) } -> LegalCategory.EMPLOYMENT
            listOf("tenant", "landlord", "kiraya", "qabza", "zameen").any { q.contains(it) } -> LegalCategory.PROPERTY
            listOf("consumer", "refund", "defective").any { q.contains(it) } -> LegalCategory.CONSUMER
            listOf("police", "arrest", "giraftar", "thana", "roka", "پولیس", "گرفتار").any { q.contains(it) } -> LegalCategory.POLICE_ARREST
            else -> LegalCategory.EMERGENCY
        }
    }

    override fun isUrgentEmergencyQuery(query: String): Boolean {
        val q = query.lowercase()
        return urgentKeywords.any { q.contains(it) }
    }

    private fun scoreCards(query: String, cards: List<LegalCard>): List<Pair<LegalCard, Int>> {
        val rawTokens = query.lowercase()
            .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length >= 2 }

        val expandedTokens = buildSet {
            rawTokens.forEach { token ->
                add(token)
                synonymMap[token]?.forEach { add(it) }
            }
        }
        val detectedCat = detectCategory(query)

        return cards.map { card ->
            var score = 0
            val titleLower = card.title.lowercase()
            val bodyLower = card.body.lowercase()
            val keywordsLower = card.keywords.map { it.lowercase() }

            expandedTokens.forEach { token ->
                if (titleLower.contains(token)) score += 4
                if (keywordsLower.any { it.contains(token) }) score += 3
                if (bodyLower.contains(token)) score += 1
            }
            if (score > 0 && card.category == detectedCat) {
                score += 2
            }
            card to score
        }.filter { it.second > 0 }
            .sortedByDescending { it.second }
    }
}

class MockAiService(
    private val ragService: RagService
) : AiService {

    private val unsafePatterns = listOf(
        "hide evidence", "destroy evidence", "fake evidence", "lie to police",
        "bribe", "evade arrest", "ignore rules", "ignore previous",
        "ثبوت مٹا", "جھوٹ بول", "saboot mita", "jhoot"
    )

    override suspend fun ask(request: AskRequest, mode: AiMode): AskResponse {
        delay(if (mode == AiMode.ONLINE) 550L else 350L)
        val strings = AppStrings.forLanguage(request.language)
        val qLower = request.question.lowercase()

        // Safety refusal check (per docs/00 & docs/05)
        if (unsafePatterns.any { qLower.contains(it) }) {
            val refusalText = when (request.language) {
                AppLanguage.EN -> "I cannot help hide, destroy, or fake evidence, or evade lawful investigation. I can share your lawful rights and how to contact a licensed advocate."
                AppLanguage.UR -> "میں ثبوت چھپانے، ضائع کرنے یا جھوٹ بولنے میں مدد نہیں کر سکتا۔ میں آپ کو صرف قانونی حقوق اور مستند وکیل سے رابطے کے طریقے بتا سکتا ہوں۔"
                AppLanguage.ROMAN_UR -> "Main saboot chhupane, mitane ya qanooni karwai se bachne mein madad nahi kar sakta. Main sirf aapke qanooni haqooq aur wakeel se raabte ke tareeqe bata sakta hun."
            }
            return AskResponse(
                answer = refusalText,
                legalCategory = LegalCategory.EMERGENCY,
                urgency = UrgencyLevel.HIGH,
                sources = emptyList(),
                nextSteps = listOf(
                    when (request.language) {
                        AppLanguage.EN -> "Preserve truthful records and consult a licensed lawyer immediately"
                        AppLanguage.UR -> "درست ریکارڈ محفوظ رکھیں اور فوراً وکیل سے مشورہ کریں"
                        AppLanguage.ROMAN_UR -> "Asal record mehfooz rakhein aur foran wakeel se mashwara karein"
                    }
                ),
                followUpQuestions = emptyList(),
                shouldContactLawyer = true,
                disclaimer = strings.legalDisclaimer,
                modeUsed = mode
            )
        }

        val matchedCards = request.cards.ifEmpty {
            ragService.retrieveForAi(request.question, request.language, topK = 3)
        }
        val isUrgent = ragService.isUrgentEmergencyQuery(request.question)

        if (matchedCards.isEmpty()) {
            val notSureText = when (request.language) {
                AppLanguage.EN -> "I am not sure because our verified HAQOOQ legal cards do not cover this specific question yet. Please consult a licensed advocate or use the emergency directory if urgent."
                AppLanguage.UR -> "مجھے اس مخصوص سوال کا یقین نہیں کیونکہ یہ ہمارے تصدیق شدہ حقوق کارڈز میں شامل نہیں۔ براہِ کرم مستند وکیل سے مشورہ کریں۔"
                AppLanguage.ROMAN_UR -> "Mujhe is khas sawal ka yaqeen nahi kyunkay yeh hamare tasdeeq shuda HAQOOQ cards mein shamil nahi. Barah-e-karam kisi mustanad wakeel se mashwara karein."
            }
            return AskResponse(
                answer = notSureText,
                legalCategory = ragService.detectCategory(request.question),
                urgency = if (isUrgent) UrgencyLevel.HIGH else UrgencyLevel.MEDIUM,
                sources = emptyList(),
                nextSteps = listOf(
                    when (request.language) {
                        AppLanguage.EN -> "Browse the Rights Library or connect with a lawyer in the Lawyers tab"
                        AppLanguage.UR -> "حقوق لائبریری دیکھیں یا وکلاء ٹیب سے وکیل سے رابطہ کریں"
                        AppLanguage.ROMAN_UR -> "Haqooq Library check karein ya Wukala tab mein wakeel se raabta karein"
                    }
                ),
                followUpQuestions = emptyList(),
                shouldContactLawyer = true,
                disclaimer = strings.legalDisclaimer,
                modeUsed = mode,
                isUrgentEmergency = isUrgent
            )
        }

        val topCard = matchedCards.first()
        val sources = matchedCards.map {
            SourceRef(cardId = it.id, title = it.title, url = it.sourceUrl)
        }

        val prefix = when (mode) {
            AiMode.ONLINE -> ""
            AiMode.OFFLINE -> when (request.language) {
                AppLanguage.EN -> "[Offline Gemma Guide] "
                AppLanguage.UR -> "[آف لائن جیما رہنمائی] "
                AppLanguage.ROMAN_UR -> "[Offline Gemma Rehnumai] "
            }
            AiMode.LIBRARY_ONLY -> when (request.language) {
                AppLanguage.EN -> "[Verified Card Summary] "
                AppLanguage.UR -> "[تصدیق شدہ کارڈ خلاصہ] "
                AppLanguage.ROMAN_UR -> "[Tasdeeq Shuda Card] "
            }
        }

        val followUps = when (request.language) {
            AppLanguage.EN -> listOf(
                "Has a formal complaint or FIR already been filed?",
                "Are you currently in a safe location?"
            )
            AppLanguage.UR -> listOf(
                "کیا ابھی تک کوئی تحریری شکایت یا ایف آئی آر درج ہوئی ہے؟",
                "کیا آپ اس وقت محفوظ جگہ پر ہیں؟"
            )
            AppLanguage.ROMAN_UR -> listOf(
                "Kya abhi tak koi tahreeri shikayat ya FIR darj hui hai?",
                "Kya aap is waqt mehfooz jagah par hain?"
            )
        }

        return AskResponse(
            answer = "$prefix${topCard.body}",
            legalCategory = topCard.category,
            urgency = if (isUrgent) UrgencyLevel.HIGH else topCard.urgencyHint,
            sources = sources,
            nextSteps = topCard.steps,
            followUpQuestions = if (mode == AiMode.LIBRARY_ONLY) emptyList() else followUps,
            shouldContactLawyer = isUrgent || topCard.urgencyHint == UrgencyLevel.HIGH,
            disclaimer = strings.legalDisclaimer,
            modeUsed = mode,
            isUrgentEmergency = isUrgent
        )
    }

    override suspend fun generateBriefing(
        language: AppLanguage,
        conversation: List<ChatMessage>
    ): LawyerBriefingPack {
        delay(250L)
        val userMsgs = conversation.filter { it.isUser }
        val summary = if (userMsgs.isEmpty()) {
            when (language) {
                AppLanguage.EN -> "Citizen requested general legal rights guidance (no specifics recorded yet)."
                AppLanguage.UR -> "شہری نے عمومی قانونی رہنمائی طلب کی۔"
                AppLanguage.ROMAN_UR -> "Shehri ne aam qanooni maloomat talab ki."
            }
        } else {
            userMsgs.joinToString(" | ") { it.text }.take(280)
        }

        val now = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(Date())
        return LawyerBriefingPack(
            summary = summary,
            timeline = userMsgs.mapIndexed { idx, msg ->
                BriefingTimelineEvent(
                    time = msg.timestampLabel.ifBlank { "Step ${idx + 1}" },
                    event = msg.text.take(120)
                )
            },
            peopleInvolved = listOf(
                when (language) {
                    AppLanguage.EN -> "Citizen (App User) — other parties marked unknown unless stated"
                    AppLanguage.UR -> "شہری (صارف) — دیگر فریقین کی تفصیل نامعلوم"
                    AppLanguage.ROMAN_UR -> "Shehri (User) — deegar afraad ki tafseel maloom nahi"
                }
            ),
            evidenceChecklist = when (language) {
                AppLanguage.EN -> listOf("Copy of CNIC", "Screenshots / messages with dates", "Written incident timeline", "Any receipt or complaint diary number")
                AppLanguage.UR -> listOf("شناختی کارڈ کی کاپی", "تاریخ کے ساتھ اسکرین شاٹس / پیغامات", "تحریری واقعات کی ترتیب", "کوئی بھی رسید یا ڈائری نمبر")
                AppLanguage.ROMAN_UR -> listOf("CNIC ki copy", "Tareekh ke saath screenshots / messages", "Waqiaat ki likhi tarteeb", "Koi bhi raseed ya diary number")
            },
            questionsForLawyer = when (language) {
                AppLanguage.EN -> listOf("What immediate lawful step should I take first?", "Which forum or police/court office applies in my city?", "What documents are needed for representation?")
                AppLanguage.UR -> listOf("مجھے سب سے پہلے کون سا قانونی قدم اٹھانا چاہیے؟", "میرے شہر میں کون سا فورم یا عدالت متعلقہ ہے؟", "نمائندگی کے لیے کون سے کاغذات درکار ہوں گے؟")
                AppLanguage.ROMAN_UR -> listOf("Mujhe sab se pehle kaunsa qanooni qadam uthana chahiye?", "Mere shehar mein kaunsa forum ya adalat mutaliqa hai?", "Case ke liye kaunse kaghzat chahiye honge?")
            },
            urgency = UrgencyLevel.HIGH,
            createdAt = now
        )
    }
}

class MockSosService : SosService {
    private val contacts = mutableListOf(
        TrustedContact(
            id = "tc_1",
            name = "Hamza Tariq",
            phone = "03001234567",
            relationship = "Brother",
            isPrimary = true
        )
    )
    private val incidents = mutableListOf<SosIncident>()

    override suspend fun getContacts(): List<TrustedContact> = contacts.toList()

    override suspend fun addContact(contact: TrustedContact): Result<TrustedContact> {
        if (contacts.size >= 5) {
            return Result.failure(IllegalStateException("MAX_CONTACTS"))
        }
        if (!isValidPakistaniPhone(contact.phone)) {
            return Result.failure(IllegalArgumentException("INVALID_PHONE"))
        }
        val normalized = contact.copy(phone = contact.phone.replace(Regex("[\\s-]"), ""))
        contacts.add(normalized)
        return Result.success(normalized)
    }

    override suspend fun updateContact(contact: TrustedContact): Result<TrustedContact> {
        if (!isValidPakistaniPhone(contact.phone)) {
            return Result.failure(IllegalArgumentException("INVALID_PHONE"))
        }
        val idx = contacts.indexOfFirst { it.id == contact.id }
        if (idx == -1) return Result.failure(IllegalArgumentException("NOT_FOUND"))
        val normalized = contact.copy(phone = contact.phone.replace(Regex("[\\s-]"), ""))
        if (normalized.isPrimary) {
            for (i in contacts.indices) {
                contacts[i] = contacts[i].copy(isPrimary = false)
            }
        }
        contacts[idx] = normalized
        return Result.success(normalized)
    }

    override suspend fun removeContact(contactId: String) {
        contacts.removeAll { it.id == contactId }
    }

    override suspend fun buildSosAlertMessage(
        language: AppLanguage,
        locationText: String?,
        batteryPercent: Int
    ): String {
        val locPart = locationText ?: "Location unavailable (GPS off)"
        return when (language) {
            AppLanguage.EN -> "EMERGENCY SOS (HAQOOQ): I may need urgent help. Location: $locPart | Battery: $batteryPercent%. Please call or reach out immediately."
            AppLanguage.UR -> "ایمرجنسی SOS (حقوق): مجھے فوری مدد کی ضرورت ہے۔ لوکیشن: $locPart | بیٹری: $batteryPercent%۔ براہِ کرم فوراً رابطہ کریں۔"
            AppLanguage.ROMAN_UR -> "EMERGENCY SOS (HAQOOQ): Mujhe fori madad ki zaroorat hai. Location: $locPart | Battery: $batteryPercent%. Barah-e-karam foran call karein."
        }
    }

    override suspend fun logIncident(incident: SosIncident) {
        incidents.add(0, incident)
    }

    override suspend fun getIncidents(): List<SosIncident> = incidents.toList()

    override suspend fun clearAll() {
        contacts.clear()
        incidents.clear()
    }

    override fun getEmergencyNumbers(): List<EmergencyContactItem> = listOf(
        EmergencyContactItem(
            id = "em_police",
            labelEn = "Police Emergency",
            labelUr = "پولیس ایمرجنسی",
            labelRoman = "Police Emergency",
            number = "15",
            noteEn = "Widely published Police helpline (verify locally)",
            noteUr = "سرکاری پولیس ہیلپ لائن (مقامی تصدیق کریں)",
            noteRoman = "Sarkari Police helpline (tasdeeq karein)"
        ),
        EmergencyContactItem(
            id = "em_rescue",
            labelEn = "Rescue / Ambulance",
            labelUr = "ریسکیو / ایمبولینس",
            labelRoman = "Rescue / Ambulance",
            number = "1122",
            noteEn = "Medical & fire rescue service (verify locally)",
            noteUr = "طبی اور ریسکیو سروس (مقامی تصدیق کریں)",
            noteRoman = "Medical aur rescue service (tasdeeq karein)"
        ),
        EmergencyContactItem(
            id = "em_mohr",
            labelEn = "Human Rights / Legal Aid Helpline",
            labelUr = "انسانی حقوق / قانونی مدد ہیلپ لائن",
            labelRoman = "Insani Haqooq / Legal Aid",
            number = "1099",
            noteEn = "Ministry of Human Rights helpline (verify locally)",
            noteUr = "وزارتِ انسانی حقوق ہیلپ لائن (تصدیق کریں)",
            noteRoman = "Ministry of Human Rights helpline (tasdeeq karein)"
        ),
        EmergencyContactItem(
            id = "em_cyber",
            labelEn = "Cyber Crime Helpline",
            labelUr = "سائبر کرائم ہیلپ لائن",
            labelRoman = "Cyber Crime Helpline",
            number = "1991",
            noteEn = "Cybercrime reporting desk (verify locally)",
            noteUr = "سائبر کرائم شکایت ڈیسک (تصدیق کریں)",
            noteRoman = "Cybercrime reporting desk (tasdeeq karein)"
        )
    )

    override fun isValidPakistaniPhone(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[\\s-]"), "")
        return Regex("^(\\+92|0)3\\d{9}$").matches(cleaned)
    }
}

class MockLawyerService : LawyerService {
    private val lawyers = listOf(
        Lawyer(
            id = "demo_lawyer_1",
            name = "Adv. Zainab Qureshi (Demo)",
            city = "Multan",
            specializations = listOf(LegalCategory.WOMEN_SAFETY, LegalCategory.DOMESTIC, LegalCategory.FIR),
            languages = listOf(AppLanguage.EN, AppLanguage.UR, AppLanguage.ROMAN_UR),
            experienceYears = 7,
            feePkr = "Free Demo / PKR 1,500",
            rating = 4.9,
            reviewCount = 28,
            bioEn = "Fictional demo profile illustrating criminal, FIR, and women-safety consultation flow.",
            bioUr = "یہ ایک ڈیمو پروفائل ہے جو خواتین کے تحفظ اور ایف آئی آر رہنمائی کے عمل کو دکھاتا ہے۔",
            bioRoman = "Yeh aik demo profile hai jo khawateen tahaffuz aur FIR consultation flow dikhata hai.",
            isDemo = true,
            reviews = listOf(
                LawyerReview("r1", "Ayesha K. (Demo)", 5, "Clear guidance on filing a workplace complaint.", "2026-11-18"),
                LawyerReview("r2", "Sana M. (Demo)", 5, "Very calm and structured briefing review.", "2026-11-10")
            )
        ),
        Lawyer(
            id = "demo_lawyer_2",
            name = "Adv. Bilal Mahmood (Demo)",
            city = "Lahore",
            specializations = listOf(LegalCategory.POLICE_ARREST, LegalCategory.BAIL_COURT, LegalCategory.FIR),
            languages = listOf(AppLanguage.EN, AppLanguage.UR, AppLanguage.ROMAN_UR),
            experienceYears = 11,
            feePkr = "Free Demo / PKR 2,000",
            rating = 4.8,
            reviewCount = 42,
            bioEn = "Fictional demo profile focused on arrest safeguards, bail procedure, and magistrate hearings.",
            bioUr = "یہ ڈیمو پروفائل ضمانت، گرفتاری کے حقوق اور فوجداری رہنمائی کے لیے ہے۔",
            bioRoman = "Yeh demo profile giraftari ke haqooq aur zamanat ke tareeqa-e-kaar ke liye hai.",
            isDemo = true,
            reviews = listOf(
                LawyerReview("r3", "Usman A. (Demo)", 5, "Explained bail steps without jargon.", "2026-11-12")
            )
        ),
        Lawyer(
            id = "demo_lawyer_3",
            name = "Adv. Hira Mansoor (Demo)",
            city = "Karachi",
            specializations = listOf(LegalCategory.CYBERCRIME, LegalCategory.EMPLOYMENT, LegalCategory.CONSUMER),
            languages = listOf(AppLanguage.EN, AppLanguage.ROMAN_UR),
            experienceYears = 5,
            feePkr = "Free Demo / PKR 1,000",
            rating = 4.7,
            reviewCount = 19,
            bioEn = "Fictional demo profile covering PECA cybercrime reporting, unpaid wages, and consumer claims.",
            bioUr = "یہ ڈیمو پروفائل سائبر کرائم، تنخواہ اور صارف حقوق کی رہنمائی کے لیے ہے۔",
            bioRoman = "Yeh demo profile cybercrime, tankhwah aur sarif haqooq ki rehnumai ke liye hai.",
            isDemo = true,
            reviews = listOf(
                LawyerReview("r4", "Ahmed R. (Demo)", 5, "Helped organize screenshots for complaint.", "2026-11-20")
            )
        ),
        Lawyer(
            id = "demo_lawyer_4",
            name = "Adv. Tariq Mehmood (Demo)",
            city = "Islamabad",
            specializations = listOf(LegalCategory.PROPERTY, LegalCategory.CONSUMER, LegalCategory.EMPLOYMENT),
            languages = listOf(AppLanguage.EN, AppLanguage.UR),
            experienceYears = 14,
            feePkr = "Free Demo / PKR 2,500",
            rating = 4.8,
            reviewCount = 31,
            bioEn = "Fictional demo profile demonstrating tenancy disputes, property possession, and civil notices.",
            bioUr = "یہ ڈیمو پروفائل کرایہ داری اور جائیداد کے تنازعات کے ڈیمو کے لیے ہے۔",
            bioRoman = "Yeh demo profile kiraya dari aur jaidad ke masail ke demo ke liye hai.",
            isDemo = true,
            reviews = emptyList()
        )
    )

    private val bookings = mutableListOf<LawyerBooking>()

    private val customReviews = mutableMapOf<String, MutableList<LawyerReview>>()

    override suspend fun getLawyers(
        city: String?,
        specialization: LegalCategory?,
        language: AppLanguage?,
        query: String
    ): List<Lawyer> {
        val q = query.trim().lowercase()
        return lawyers.map { l ->
            val extra = customReviews[l.id].orEmpty()
            if (extra.isEmpty()) l else l.copy(reviews = extra + l.reviews, reviewCount = extra.size + l.reviews.size)
        }.filter { lawyer ->
            (city.isNullOrBlank() || lawyer.city.equals(city, ignoreCase = true)) &&
                (specialization == null || specialization == LegalCategory.ALL || specialization in lawyer.specializations) &&
                (language == null || language in lawyer.languages) &&
                (q.isEmpty() || lawyer.name.lowercase().contains(q) || lawyer.city.lowercase().contains(q) ||
                    lawyer.bioEn.lowercase().contains(q) || lawyer.bioRoman.lowercase().contains(q))
        }
    }

    override suspend fun getLawyerById(id: String): Lawyer? =
        getLawyers().firstOrNull { it.id == id }

    override suspend fun bookConsultation(
        lawyer: Lawyer,
        dateLabel: String,
        timeSlot: String,
        topic: String,
        briefing: LawyerBriefingPack?
    ): LawyerBooking {
        delay(250L)
        val booking = LawyerBooking(
            lawyerId = lawyer.id,
            lawyerName = lawyer.name,
            dateLabel = dateLabel,
            timeSlot = timeSlot,
            topic = topic.ifBlank { "General Consultation (Demo)" },
            status = "Confirmed (Demo)",
            attachedBriefingTitle = briefing?.summary?.take(48)
        )
        bookings.add(0, booking)
        return booking
    }

    override suspend fun getBookings(): List<LawyerBooking> = bookings.toList()

    override suspend fun addDemoReview(
        lawyerId: String,
        authorName: String,
        rating: Int,
        comment: String
    ): Result<LawyerReview> {
        if (bookings.none { it.lawyerId == lawyerId }) {
            return Result.failure(IllegalStateException("BOOKING_REQUIRED"))
        }
        val clean = comment.trim()
        if (clean.isEmpty()) return Result.failure(IllegalArgumentException("EMPTY_COMMENT"))
        val review = LawyerReview(
            id = "rev_${System.currentTimeMillis()}",
            authorName = "${authorName.trim().ifBlank { "Citizen" }} (Demo)",
            rating = rating.coerceIn(1, 5),
            comment = clean,
            date = "2026-11-27"
        )
        val list = customReviews.getOrPut(lawyerId) { mutableListOf() }
        list.add(0, review)
        return Result.success(review)
    }

    override suspend fun clearAll() {
        bookings.clear()
        customReviews.clear()
    }

    override suspend fun sendDemoLawyerReply(
        lawyer: Lawyer,
        userMessage: String,
        language: AppLanguage
    ): String {
        delay(600L)
        return when (language) {
            AppLanguage.EN -> "[Demo Account - Not a Real Lawyer] Thank you for sharing. Based on your summary, keep all dated screenshots/receipts ready. In a full release, ${lawyer.name.removeSuffix(" (Demo)")} would review jurisdiction in ${lawyer.city} before advising."
            AppLanguage.UR -> "[ڈیمو اکاؤنٹ — حقیقی وکیل نہیں] تفصیلات شیئر کرنے کا شکریہ۔ تمام تاریخ وار اسکرین شاٹس اور رسیدیں محفوظ رکھیں۔ مکمل ورژن میں ${lawyer.city} کی متعلقہ عدالت یا فورم کا جائزہ لیا جائے گا۔"
            AppLanguage.ROMAN_UR -> "[Demo Account - Not a Real Lawyer] Tafseel share karne ka shukriya. Tamam screenshots aur raseedein tareekh ke saath mehfooz rakhein. Mukammal app mein ${lawyer.city} ke mutaliqa forum ka jaiza liya jata hai."
        }
    }
}
