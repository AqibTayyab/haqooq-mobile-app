package com.example.contracts

import java.util.UUID

/**
 * Supported languages in HAQOOQ.
 * - EN: English (LTR)
 * - UR: Urdu script (RTL)
 * - ROMAN_UR: Roman Urdu (LTR)
 */
enum class AppLanguage(val code: String, val nativeLabel: String, val isRtl: Boolean) {
    EN("en", "English", false),
    UR("ur", "اردو", true),
    ROMAN_UR("roman_ur", "Roman Urdu", false);

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: EN
    }
}

enum class AiMode {
    ONLINE,       // Gemini via backend proxy
    OFFLINE,      // On-device Gemma + local RAG
    LIBRARY_ONLY  // Verified offline legal cards fallback
}

enum class UrgencyLevel {
    LOW,
    MEDIUM,
    HIGH
}

enum class LegalCategory(val id: String) {
    ALL("all"),
    POLICE_ARREST("police_arrest"),
    FIR("fir"),
    BAIL_COURT("bail_court"),
    WOMEN_SAFETY("women_safety"),
    DOMESTIC("domestic"),
    CYBERCRIME("cybercrime"),
    EMPLOYMENT("employment"),
    PROPERTY("property"),
    CONSUMER("consumer"),
    EMERGENCY("emergency")
}

data class LegalCard(
    val id: String,
    val title: String,
    val category: LegalCategory,
    val lang: AppLanguage,
    val body: String,
    val steps: List<String>,
    val sourceName: String,
    val sourceUrl: String,
    val lastReviewed: String,
    val reviewer: String,
    val urgencyHint: UrgencyLevel,
    val keywords: List<String>,
    val isDraft: Boolean = false
)

data class SourceRef(
    val cardId: String? = null,
    val title: String,
    val url: String
)

data class AskContext(
    val detained: Boolean = false,
    val firRegistered: Boolean = false,
    val violence: Boolean = false
)

data class AskRequest(
    val question: String,
    val language: AppLanguage,
    val city: String = "Multan",
    val context: AskContext = AskContext(),
    val cards: List<LegalCard> = emptyList()
)

data class AskResponse(
    val answer: String,
    val legalCategory: LegalCategory,
    val urgency: UrgencyLevel,
    val sources: List<SourceRef>,
    val nextSteps: List<String>,
    val followUpQuestions: List<String>,
    val shouldContactLawyer: Boolean,
    val disclaimer: String,
    val modeUsed: AiMode,
    val isUrgentEmergency: Boolean = false
)

data class BriefingTimelineEvent(
    val time: String,
    val event: String
)

data class LawyerBriefingPack(
    val id: String = UUID.randomUUID().toString(),
    val summary: String,
    val timeline: List<BriefingTimelineEvent>,
    val peopleInvolved: List<String>,
    val evidenceChecklist: List<String>,
    val questionsForLawyer: List<String>,
    val urgency: UrgencyLevel,
    val createdAt: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val response: AskResponse? = null,
    val timestampLabel: String = ""
)

data class CaseNote(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val summary: String,
    val category: LegalCategory,
    val createdAt: String,
    val briefingPack: LawyerBriefingPack? = null
)

data class TrustedContact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean = false
)

data class EmergencyContactItem(
    val id: String,
    val labelEn: String,
    val labelUr: String,
    val labelRoman: String,
    val number: String,
    val noteEn: String,
    val noteUr: String,
    val noteRoman: String,
    val isVerified: Boolean = true
)

data class SosIncident(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val locationText: String,
    val batteryPercent: Int,
    val messagePreview: String,
    val recipientCount: Int,
    val status: String
)

data class SafetyCheckInState(
    val isActive: Boolean = false,
    val durationMinutes: Int = 15,
    val remainingSeconds: Int = 0,
    val expiredTriggered: Boolean = false
)

data class LawyerReview(
    val id: String,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class Lawyer(
    val id: String,
    val name: String,
    val city: String,
    val specializations: List<LegalCategory>,
    val languages: List<AppLanguage>,
    val experienceYears: Int,
    val feePkr: String,
    val rating: Double,
    val reviewCount: Int,
    val bioEn: String,
    val bioUr: String,
    val bioRoman: String,
    val isDemo: Boolean = true, // Mandatory per CRITICAL RULES
    val reviews: List<LawyerReview> = emptyList()
)

data class LawyerBooking(
    val id: String = UUID.randomUUID().toString(),
    val lawyerId: String,
    val lawyerName: String,
    val dateLabel: String,
    val timeSlot: String,
    val topic: String,
    val status: String,
    val attachedBriefingTitle: String? = null
)

data class LawyerChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestampLabel: String,
    val attachedBriefing: LawyerBriefingPack? = null
)

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
    data class Error(val message: String) : UiState<Nothing>()
    data class Offline(val fallbackMessage: String) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
