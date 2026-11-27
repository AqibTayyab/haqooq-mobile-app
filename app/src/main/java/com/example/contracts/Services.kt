package com.example.contracts

import kotlinx.coroutines.flow.StateFlow

/**
 * Shared Service Interfaces (Member 1 Architecture Contracts).
 * Members 2, 3, and 4 implement these contracts independently; UI screens code ONLY against them.
 */
interface RagService {
    suspend fun getAllCards(lang: AppLanguage): List<LegalCard>
    suspend fun searchCards(query: String, category: LegalCategory, lang: AppLanguage): List<LegalCard>
    suspend fun retrieveForAi(query: String, lang: AppLanguage, topK: Int = 3): List<LegalCard>
    fun detectCategory(query: String): LegalCategory
    fun isUrgentEmergencyQuery(query: String): Boolean
}

interface AiService {
    suspend fun ask(request: AskRequest, mode: AiMode): AskResponse
    suspend fun generateBriefing(
        language: AppLanguage,
        conversation: List<ChatMessage>
    ): LawyerBriefingPack
}

interface SosService {
    suspend fun getContacts(): List<TrustedContact>
    suspend fun addContact(contact: TrustedContact): Result<TrustedContact>
    suspend fun updateContact(contact: TrustedContact): Result<TrustedContact>
    suspend fun removeContact(contactId: String)
    suspend fun buildSosAlertMessage(
        language: AppLanguage,
        locationText: String?,
        batteryPercent: Int
    ): String
    suspend fun logIncident(incident: SosIncident)
    suspend fun getIncidents(): List<SosIncident>
    suspend fun clearAll()
    fun getEmergencyNumbers(): List<EmergencyContactItem>
    fun isValidPakistaniPhone(phone: String): Boolean
}

interface LawyerService {
    suspend fun getLawyers(
        city: String? = null,
        specialization: LegalCategory? = null,
        language: AppLanguage? = null,
        query: String = ""
    ): List<Lawyer>
    suspend fun getLawyerById(id: String): Lawyer?
    suspend fun bookConsultation(
        lawyer: Lawyer,
        dateLabel: String,
        timeSlot: String,
        topic: String,
        briefing: LawyerBriefingPack?
    ): LawyerBooking
    suspend fun getBookings(): List<LawyerBooking>
    suspend fun addDemoReview(
        lawyerId: String,
        authorName: String,
        rating: Int,
        comment: String
    ): Result<LawyerReview>
    suspend fun clearAll()
    suspend fun sendDemoLawyerReply(
        lawyer: Lawyer,
        userMessage: String,
        language: AppLanguage
    ): String
}

interface ConnectivityService {
    val activeMode: StateFlow<AiMode>
    fun setModeOverride(mode: AiMode?)
    fun setSimulatedOffline(offline: Boolean)
}
