package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import com.example.contracts.AiMode
import com.example.contracts.AppLanguage
import com.example.contracts.AskRequest
import com.example.contracts.CaseNote
import com.example.contracts.ChatMessage
import com.example.contracts.EmergencyContactItem
import com.example.contracts.Lawyer
import com.example.contracts.LawyerBooking
import com.example.contracts.LawyerBriefingPack
import com.example.contracts.LawyerChatMessage
import com.example.contracts.LawyerReview
import com.example.contracts.LegalCard
import com.example.contracts.LegalCategory
import com.example.contracts.SafetyCheckInState
import com.example.contracts.ServiceLocator
import com.example.contracts.SosIncident
import com.example.contracts.TrustedContact
import com.example.contracts.UiState
import com.example.l10n.AppStrings
import com.example.l10n.LocaleController
import com.example.services.sos.SosDeviceHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HaqooqViewModel(application: Application) : AndroidViewModel(application) {

    init {
        ServiceLocator.init(application)
    }

    private val localeController = LocaleController(application)
    private val connectivity = ServiceLocator.connectivityService
    private val rag = ServiceLocator.ragService
    private val ai = ServiceLocator.aiService
    private val sos = ServiceLocator.sosService
    private val lawyerService = ServiceLocator.lawyerService

    val language: StateFlow<AppLanguage> = localeController.language
    val isDarkMode: StateFlow<Boolean> = localeController.isDarkMode
    val hasCompletedOnboarding: StateFlow<Boolean> = localeController.hasCompletedOnboarding
    val liteModeEnabled: StateFlow<Boolean> = localeController.liteModeEnabled
    val activeMode: StateFlow<AiMode> = connectivity.activeMode

    // Chat state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private var lastFailedQuestion: String? = null

    private val _latestBriefing = MutableStateFlow<LawyerBriefingPack?>(null)
    val latestBriefing: StateFlow<LawyerBriefingPack?> = _latestBriefing.asStateFlow()

    // Case Notes
    private val _caseNotes = MutableStateFlow<List<CaseNote>>(emptyList())
    val caseNotes: StateFlow<List<CaseNote>> = _caseNotes.asStateFlow()

    // Rights Library
    private val _rightsQuery = MutableStateFlow("")
    val rightsQuery: StateFlow<String> = _rightsQuery.asStateFlow()

    private val _selectedRightsCategory = MutableStateFlow(LegalCategory.ALL)
    val selectedRightsCategory: StateFlow<LegalCategory> = _selectedRightsCategory.asStateFlow()

    private val _rightsState = MutableStateFlow<UiState<List<LegalCard>>>(UiState.Loading)
    val rightsState: StateFlow<UiState<List<LegalCard>>> = _rightsState.asStateFlow()

    // SOS & Safety Check-In
    private val _trustedContacts = MutableStateFlow<List<TrustedContact>>(emptyList())
    val trustedContacts: StateFlow<List<TrustedContact>> = _trustedContacts.asStateFlow()

    private val _sosIncidents = MutableStateFlow<List<SosIncident>>(emptyList())
    val sosIncidents: StateFlow<List<SosIncident>> = _sosIncidents.asStateFlow()

    private val _sosCountdown = MutableStateFlow<Int?>(null)
    val sosCountdown: StateFlow<Int?> = _sosCountdown.asStateFlow()

    private val _sosPreparedMessage = MutableStateFlow<String?>(null)
    val sosPreparedMessage: StateFlow<String?> = _sosPreparedMessage.asStateFlow()

    private val _checkInState = MutableStateFlow(SafetyCheckInState())
    val checkInState: StateFlow<SafetyCheckInState> = _checkInState.asStateFlow()

    val emergencyNumbers: List<EmergencyContactItem> = sos.getEmergencyNumbers()

    // Lawyers Marketplace
    private val _lawyersState = MutableStateFlow<UiState<List<Lawyer>>>(UiState.Loading)
    val lawyersState: StateFlow<UiState<List<Lawyer>>> = _lawyersState.asStateFlow()

    private val _selectedCity = MutableStateFlow<String?>(null)
    val selectedCity: StateFlow<String?> = _selectedCity.asStateFlow()

    private val _selectedLawyerCategory = MutableStateFlow(LegalCategory.ALL)
    val selectedLawyerCategory: StateFlow<LegalCategory> = _selectedLawyerCategory.asStateFlow()

    private val _selectedLawyerLanguage = MutableStateFlow<AppLanguage?>(null)
    val selectedLawyerLanguage: StateFlow<AppLanguage?> = _selectedLawyerLanguage.asStateFlow()

    private val _lawyerSearchQuery = MutableStateFlow("")
    val lawyerSearchQuery: StateFlow<String> = _lawyerSearchQuery.asStateFlow()

    private val _bookings = MutableStateFlow<List<LawyerBooking>>(emptyList())
    val bookings: StateFlow<List<LawyerBooking>> = _bookings.asStateFlow()

    private val _lawyerChats = MutableStateFlow<Map<String, List<LawyerChatMessage>>>(emptyMap())
    val lawyerChats: StateFlow<Map<String, List<LawyerChatMessage>>> = _lawyerChats.asStateFlow()

    private val _isLawyerTyping = MutableStateFlow(false)
    val isLawyerTyping: StateFlow<Boolean> = _isLawyerTyping.asStateFlow()

    private var countdownJob: Job? = null
    private var checkInJob: Job? = null

    init {
        refreshRights()
        refreshSosData()
        refreshLawyers()
        restorePendingCheckIn()
    }

    fun setLanguage(lang: AppLanguage) {
        localeController.setLanguage(lang)
        refreshRights()
    }

    fun setDarkMode(enabled: Boolean) {
        localeController.setDarkMode(enabled)
    }

    fun toggleDarkMode() {
        localeController.toggleDarkMode()
    }

    fun completeOnboarding() {
        localeController.completeOnboarding()
    }

    fun setModeOverride(mode: AiMode?) {
        connectivity.setModeOverride(mode)
    }

    fun setLiteMode(enabled: Boolean) {
        localeController.setLiteMode(enabled)
        if (enabled) {
            connectivity.setModeOverride(AiMode.LIBRARY_ONLY)
        } else {
            connectivity.setModeOverride(null)
        }
    }

    // --- Rights Library ---
    fun updateRightsSearch(query: String) {
        _rightsQuery.value = query
        refreshRights()
    }

    fun selectRightsCategory(category: LegalCategory) {
        _selectedRightsCategory.value = category
        refreshRights()
    }

    fun refreshRights() {
        viewModelScope.launch {
            _rightsState.value = UiState.Loading
            val results = rag.searchCards(
                query = _rightsQuery.value,
                category = _selectedRightsCategory.value,
                lang = language.value
            )
            _rightsState.value = if (results.isEmpty()) UiState.Empty else UiState.Success(results)
        }
    }

    suspend fun findCardById(cardId: String): LegalCard? {
        return rag.getAllCards(language.value).firstOrNull { it.id == cardId }
            ?: rag.getAllCards(AppLanguage.EN).firstOrNull { it.id == cardId }
    }

    // --- AI Chat ---
    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _isChatLoading.value) return

        val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(isUser = true, text = trimmed, timestampLabel = timeNow)
        _chatMessages.value = _chatMessages.value + userMsg
        _chatError.value = null
        lastFailedQuestion = null
        executeChatRequest(question = trimmed, forcedMode = null)
    }

    fun retryLastChatMessage() {
        val question = lastFailedQuestion ?: return
        if (_isChatLoading.value) return
        _chatError.value = null
        executeChatRequest(question = question, forcedMode = null)
    }

    fun answerLastQuestionOffline() {
        val question = lastFailedQuestion ?: return
        if (_isChatLoading.value) return
        _chatError.value = null
        executeChatRequest(question = question, forcedMode = AiMode.OFFLINE)
    }

    fun dismissChatError() {
        _chatError.value = null
    }

    private fun executeChatRequest(question: String, forcedMode: AiMode?) {
        _isChatLoading.value = true
        viewModelScope.launch {
            try {
                val currentLang = language.value
                val mode = forcedMode ?: if (liteModeEnabled.value) AiMode.LIBRARY_ONLY else activeMode.value
                val retrieved = rag.retrieveForAi(question, currentLang, topK = 3)
                val response = ai.ask(
                    request = AskRequest(
                        question = question,
                        language = currentLang,
                        cards = retrieved
                    ),
                    mode = mode
                )
                val botMsg = ChatMessage(
                    isUser = false,
                    text = response.answer,
                    response = response,
                    timestampLabel = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
                _chatMessages.value = _chatMessages.value + botMsg
                lastFailedQuestion = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("HaqooqViewModel", "AI chat request failed", e)
                lastFailedQuestion = question
                _chatError.value = e.localizedMessage ?: "Online AI request failed."
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        _chatError.value = null
        lastFailedQuestion = null
    }

    fun saveNoteFromMessage(msg: ChatMessage) {
        val resp = msg.response ?: return
        val title = resp.sources.firstOrNull()?.title ?: msg.text.take(44)
        val note = CaseNote(
            title = title,
            summary = resp.answer.take(220),
            category = resp.legalCategory,
            createdAt = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date()),
            briefingPack = _latestBriefing.value
        )
        _caseNotes.value = listOf(note) + _caseNotes.value
    }

    fun createBriefingPack(onReady: (LawyerBriefingPack) -> Unit) {
        viewModelScope.launch {
            val pack = ai.generateBriefing(language.value, _chatMessages.value)
            _latestBriefing.value = pack
            onReady(pack)
        }
    }

    // --- SOS & Safety Check-In ---
    fun refreshSosData() {
        viewModelScope.launch {
            _trustedContacts.value = sos.getContacts()
            _sosIncidents.value = sos.getIncidents()
        }
    }

    fun addTrustedContact(
        name: String,
        phone: String,
        relationship: String,
        isPrimary: Boolean = false,
        onResult: (Result<TrustedContact>) -> Unit
    ) {
        viewModelScope.launch {
            val result = sos.addContact(
                TrustedContact(
                    name = name.trim(),
                    phone = phone.trim(),
                    relationship = relationship.trim().ifBlank { "Family" },
                    isPrimary = isPrimary
                )
            )
            _trustedContacts.value = sos.getContacts()
            onResult(result)
        }
    }

    fun updateTrustedContact(
        contact: TrustedContact,
        onResult: (Result<TrustedContact>) -> Unit
    ) {
        viewModelScope.launch {
            val result = sos.updateContact(
                contact.copy(
                    name = contact.name.trim(),
                    phone = contact.phone.trim(),
                    relationship = contact.relationship.trim().ifBlank { "Family" }
                )
            )
            _trustedContacts.value = sos.getContacts()
            onResult(result)
        }
    }

    fun removeTrustedContact(id: String) {
        viewModelScope.launch {
            sos.removeContact(id)
            _trustedContacts.value = sos.getContacts()
        }
    }

    fun isValidPhone(phone: String): Boolean = sos.isValidPakistaniPhone(phone)

    fun startSosCountdown() {
        if (_sosCountdown.value != null) return
        _sosPreparedMessage.value = null
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (sec in 3 downTo 1) {
                _sosCountdown.value = sec
                SosDeviceHelper.vibrateTick(getApplication(), isFinalAlert = false)
                delay(1000L)
            }
            _sosCountdown.value = null
            SosDeviceHelper.vibrateTick(getApplication(), isFinalAlert = true)
            prepareSosAlert()
        }
    }

    fun cancelSosCountdown() {
        countdownJob?.cancel()
        _sosCountdown.value = null
    }

    fun dismissPreparedSos() {
        _sosPreparedMessage.value = null
    }

    private suspend fun prepareSosAlert() {
        val app = getApplication<Application>()
        val locResult = SosDeviceHelper.fetchLocationWithFallback(app)
        val battery = SosDeviceHelper.readBatteryPercent(app)
        val msg = sos.buildSosAlertMessage(
            language = language.value,
            locationText = locResult.mapsLink,
            batteryPercent = battery
        )
        _sosPreparedMessage.value = msg
        sos.logIncident(
            SosIncident(
                timestamp = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date()),
                locationText = locResult.mapsLink ?: locResult.readableStatus,
                batteryPercent = battery,
                messagePreview = msg,
                recipientCount = _trustedContacts.value.size,
                status = if (locResult.mapsLink != null) {
                    "Prepared with GPS (Tap to Send)"
                } else {
                    "Prepared without GPS (${locResult.readableStatus})"
                }
            )
        )
        _sosIncidents.value = sos.getIncidents()
    }

    fun startCheckIn(minutes: Int) {
        val totalSecs = minutes * 60
        val deadlineMs = System.currentTimeMillis() + totalSecs * 1000L
        SosDeviceHelper.persistCheckInDeadline(getApplication(), deadlineMs, minutes)
        launchCheckInCountdown(minutes = minutes, initialRemainingSeconds = totalSecs)
    }

    private fun restorePendingCheckIn() {
        val saved = SosDeviceHelper.getSavedCheckInState(getApplication()) ?: return
        val (deadlineMs, durationMin) = saved
        val remainingSecs = ((deadlineMs - System.currentTimeMillis()) / 1000L).toInt()
        if (remainingSecs > 0) {
            launchCheckInCountdown(minutes = durationMin, initialRemainingSeconds = remainingSecs)
        } else {
            SosDeviceHelper.clearCheckInDeadline(getApplication())
            viewModelScope.launch {
                triggerCheckInExpired()
            }
        }
    }

    private fun launchCheckInCountdown(minutes: Int, initialRemainingSeconds: Int) {
        checkInJob?.cancel()
        _checkInState.value = SafetyCheckInState(
            isActive = true,
            durationMinutes = minutes,
            remainingSeconds = initialRemainingSeconds,
            expiredTriggered = false
        )
        checkInJob = viewModelScope.launch {
            var remaining = initialRemainingSeconds
            while (remaining > 0) {
                delay(1000L)
                remaining -= 1
                _checkInState.value = _checkInState.value.copy(remainingSeconds = remaining)
            }
            SosDeviceHelper.clearCheckInDeadline(getApplication())
            triggerCheckInExpired()
        }
    }

    private suspend fun triggerCheckInExpired() {
        _checkInState.value = _checkInState.value.copy(
            isActive = false,
            remainingSeconds = 0,
            expiredTriggered = true
        )
        val strings = AppStrings.forLanguage(language.value)
        SosDeviceHelper.vibrateTick(getApplication(), isFinalAlert = true)
        SosDeviceHelper.showCheckInExpiredNotification(
            context = getApplication(),
            title = strings.safetyCheckInTitle,
            body = strings.checkInExpiredWarning
        )
        prepareSosAlert()
    }

    fun confirmCheckInSafe() {
        checkInJob?.cancel()
        SosDeviceHelper.clearCheckInDeadline(getApplication())
        _checkInState.value = SafetyCheckInState()
    }

    // --- Lawyers Marketplace ---
    fun refreshLawyers() {
        viewModelScope.launch {
            _lawyersState.value = UiState.Loading
            val list = lawyerService.getLawyers(
                city = _selectedCity.value,
                specialization = _selectedLawyerCategory.value,
                language = _selectedLawyerLanguage.value,
                query = _lawyerSearchQuery.value
            )
            _lawyersState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            _bookings.value = lawyerService.getBookings()
        }
    }

    fun setLawyerSearchQuery(query: String) {
        _lawyerSearchQuery.value = query
        refreshLawyers()
    }

    fun setCityFilter(city: String?) {
        _selectedCity.value = city
        refreshLawyers()
    }

    fun setLawyerCategoryFilter(category: LegalCategory) {
        _selectedLawyerCategory.value = category
        refreshLawyers()
    }

    fun setLawyerLanguageFilter(lang: AppLanguage?) {
        _selectedLawyerLanguage.value = lang
        refreshLawyers()
    }

    suspend fun getLawyerById(id: String): Lawyer? = lawyerService.getLawyerById(id)

    fun bookDemoConsultation(
        lawyer: Lawyer,
        dateLabel: String,
        timeSlot: String,
        topic: String,
        attachBriefing: Boolean,
        onConfirmed: (LawyerBooking) -> Unit
    ) {
        viewModelScope.launch {
            val booking = lawyerService.bookConsultation(
                lawyer = lawyer,
                dateLabel = dateLabel,
                timeSlot = timeSlot,
                topic = topic,
                briefing = if (attachBriefing) _latestBriefing.value else null
            )
            _bookings.value = lawyerService.getBookings()
            onConfirmed(booking)
        }
    }

    fun submitDemoReview(
        lawyerId: String,
        authorName: String,
        rating: Int,
        comment: String,
        onResult: (Result<LawyerReview>) -> Unit
    ) {
        viewModelScope.launch {
            val result = lawyerService.addDemoReview(lawyerId, authorName, rating, comment)
            if (result.isSuccess) {
                refreshLawyers()
            }
            onResult(result)
        }
    }

    fun sendDemoLawyerMessage(
        lawyer: Lawyer,
        text: String,
        attachedBriefing: LawyerBriefingPack? = null
    ) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() && attachedBriefing == null) return
        val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMessage = LawyerChatMessage(
            isUser = true,
            text = trimmed.ifEmpty { "Shared Lawyer Briefing Pack" },
            timestampLabel = now,
            attachedBriefing = attachedBriefing
        )
        val existing = _lawyerChats.value[lawyer.id].orEmpty()
        _lawyerChats.value = _lawyerChats.value + (lawyer.id to (existing + userMessage))
        _isLawyerTyping.value = true

        viewModelScope.launch {
            val replyInput = if (attachedBriefing != null) {
                "Briefing attached: ${attachedBriefing.summary}"
            } else {
                trimmed
            }
            val reply = lawyerService.sendDemoLawyerReply(lawyer, replyInput, language.value)
            val botMessage = LawyerChatMessage(
                isUser = false,
                text = reply,
                timestampLabel = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
            val updated = _lawyerChats.value[lawyer.id].orEmpty()
            _lawyerChats.value = _lawyerChats.value + (lawyer.id to (updated + botMessage))
            _isLawyerTyping.value = false
        }
    }

    fun deleteAllUserData() {
        _chatMessages.value = emptyList()
        _latestBriefing.value = null
        _caseNotes.value = emptyList()
        _sosPreparedMessage.value = null
        _lawyerChats.value = emptyMap()
        confirmCheckInSafe()
        viewModelScope.launch {
            sos.clearAll()
            lawyerService.clearAll()
            refreshSosData()
            refreshLawyers()
        }
    }
}
