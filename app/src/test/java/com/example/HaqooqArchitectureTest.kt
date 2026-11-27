package com.example

import com.example.contracts.AiMode
import com.example.contracts.AppLanguage
import com.example.contracts.AskRequest
import com.example.contracts.LegalCategory
import com.example.data.MockAiService
import com.example.data.MockRagService
import com.example.data.MockSosService
import com.example.l10n.AppStrings
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HaqooqArchitectureTest {

    @Test
    fun `multilingual strings and RTL flags match all three locales`() {
        assertFalse(AppLanguage.EN.isRtl)
        assertTrue(AppLanguage.UR.isRtl)
        assertFalse(AppLanguage.ROMAN_UR.isRtl)

        val en = AppStrings.forLanguage(AppLanguage.EN)
        val ur = AppStrings.forLanguage(AppLanguage.UR)
        val ru = AppStrings.forLanguage(AppLanguage.ROMAN_UR)

        assertEquals("HAQOOQ", en.appName)
        assertEquals("حقوق", ur.appName)
        assertEquals("HAQOOQ", ru.appName)
        assertTrue(en.legalDisclaimer.isNotBlank())
        assertTrue(ur.legalDisclaimer.isNotBlank())
        assertTrue(ru.legalDisclaimer.isNotBlank())
        assertTrue(en.settingsDarkModeTitle.isNotBlank())
        assertTrue(ur.settingsDarkModeTitle.isNotBlank())
        assertTrue(ru.settingsDarkModeTitle.isNotBlank())
    }

    @Test
    fun `rag retrieves police cards for roman urdu query`() = runTest {
        val rag = MockRagService()
        val cards = rag.retrieveForAi("police ne mujhe roka hai", AppLanguage.ROMAN_UR)
        assertTrue(cards.isNotEmpty())
        assertEquals(LegalCategory.POLICE_ARREST, cards.first().category)
    }

    @Test
    fun `ai refuses evidence hiding and preserves disclaimer`() = runTest {
        val rag = MockRagService()
        val ai = MockAiService(rag)
        val response = ai.ask(
            AskRequest(
                question = "How do I hide evidence from police?",
                language = AppLanguage.EN
            ),
            AiMode.ONLINE
        )
        assertTrue(response.sources.isEmpty())
        assertTrue(response.shouldContactLawyer)
        assertEquals(AppStrings.forLanguage(AppLanguage.EN).legalDisclaimer, response.disclaimer)
    }

    @Test
    fun `sos validates pakistani phone numbers`() {
        val sos = MockSosService()
        assertTrue(sos.isValidPakistaniPhone("03001234567"))
        assertTrue(sos.isValidPakistaniPhone("+923001234567"))
        assertTrue(sos.isValidPakistaniPhone("0300-1234567"))
        assertFalse(sos.isValidPakistaniPhone("12345"))
        assertFalse(sos.isValidPakistaniPhone("+15551234567"))
    }
}
