package com.example.l10n

import android.content.Context
import android.content.SharedPreferences
import com.example.contracts.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists user language choice, dark/light theme preference, onboarding state, and lite-mode.
 */
class LocaleController(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString(KEY_LANGUAGE, AppLanguage.EN.code))
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Black dark mode is enabled by default for eye comfort; user can toggle anytime.
    private val _isDarkMode = MutableStateFlow(
        prefs.getBoolean(KEY_DARK_MODE, true)
    )
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(
        prefs.getBoolean(KEY_ONBOARDING_DONE, false)
    )
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _liteModeEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_LITE_MODE, false)
    )
    val liteModeEnabled: StateFlow<Boolean> = _liteModeEnabled.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
        _language.value = lang
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }

    fun toggleDarkMode() {
        setDarkMode(!_isDarkMode.value)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
        _hasCompletedOnboarding.value = true
    }

    fun setLiteMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LITE_MODE, enabled).apply()
        _liteModeEnabled.value = enabled
    }

    companion object {
        private const val PREFS_NAME = "haqooq_prefs"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_LITE_MODE = "lite_mode_enabled"
    }
}
