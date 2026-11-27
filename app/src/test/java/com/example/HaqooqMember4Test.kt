package com.example

import com.example.contracts.AppLanguage
import com.example.contracts.BriefingTimelineEvent
import com.example.contracts.LawyerBriefingPack
import com.example.contracts.LegalCategory
import com.example.contracts.SafetyCheckInState
import com.example.contracts.SosIncident
import com.example.contracts.TrustedContact
import com.example.contracts.UrgencyLevel
import com.example.data.MockLawyerService
import com.example.data.MockSosService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HaqooqMember4Test {

    @Test
    fun `sos validates pakistani mobile formats and rejects non-pk numbers`() {
        val sos = MockSosService()
        assertTrue(sos.isValidPakistaniPhone("03001234567"))
        assertTrue(sos.isValidPakistaniPhone("+923219876543"))
        assertTrue(sos.isValidPakistaniPhone("0333-1122334"))
        assertTrue(sos.isValidPakistaniPhone("+92 345 1234567"))

        assertFalse(sos.isValidPakistaniPhone("0421234567"))
        assertFalse(sos.isValidPakistaniPhone("+15551234567"))
        assertFalse(sos.isValidPakistaniPhone("0300123"))
        assertFalse(sos.isValidPakistaniPhone(""))
    }

    @Test
    fun `sos enforces max 5 trusted contacts and supports update and primary switch`() = runTest {
        val sos = MockSosService()
        // Seeded with 1 contact -> add 4 more to reach 5
        for (i in 2..5) {
            val res = sos.addContact(
                TrustedContact(
                    id = "tc_$i",
                    name = "Contact $i",
                    phone = "0300123456$i",
                    relationship = "Family"
                )
            )
            assertTrue(res.isSuccess)
        }
        assertEquals(5, sos.getContacts().size)

        // 6th contact must fail with MAX_CONTACTS
        val sixth = sos.addContact(
            TrustedContact(
                id = "tc_6",
                name = "Extra",
                phone = "03001234569",
                relationship = "Friend"
            )
        )
        assertTrue(sixth.isFailure)
        assertEquals("MAX_CONTACTS", sixth.exceptionOrNull()?.message)

        // Update tc_2 as primary and verify tc_1 is no longer primary
        val tc2 = sos.getContacts().first { it.id == "tc_2" }
        val updated = sos.updateContact(tc2.copy(name = "Updated Contact 2", isPrimary = true))
        assertTrue(updated.isSuccess)

        val all = sos.getContacts()
        assertEquals("Updated Contact 2", all.first { it.id == "tc_2" }.name)
        assertTrue(all.first { it.id == "tc_2" }.isPrimary)
        assertFalse(all.first { it.id == "tc_1" }.isPrimary)

        sos.removeContact("tc_2")
        assertEquals(4, sos.getContacts().size)
    }

    @Test
    fun `sos builds multilingual alert messages with maps link and gps fallback`() = runTest {
        val sos = MockSosService()
        val mapsLink = "https://maps.google.com/?q=30.1575,71.5249"

        val enWithGps = sos.buildSosAlertMessage(AppLanguage.EN, mapsLink, 82)
        val urWithGps = sos.buildSosAlertMessage(AppLanguage.UR, mapsLink, 82)
        val romanWithGps = sos.buildSosAlertMessage(AppLanguage.ROMAN_UR, mapsLink, 82)

        assertTrue(enWithGps.contains(mapsLink))
        assertTrue(enWithGps.contains("82%"))
        assertTrue(urWithGps.contains(mapsLink))
        assertTrue(romanWithGps.contains(mapsLink))

        val enNoGps = sos.buildSosAlertMessage(AppLanguage.EN, null, 19)
        assertTrue(enNoGps.contains("Location unavailable"))
        assertTrue(enNoGps.contains("19%"))
    }

    @Test
    fun `sos emergency helplines include expected numbers and local verification notices`() = runTest {
        val sos = MockSosService()
        val numbers = sos.getEmergencyNumbers()
        assertEquals(listOf("15", "1122", "1099", "1991"), numbers.map { it.number })
        numbers.forEach { item ->
            assertTrue(item.noteEn.contains("verify", ignoreCase = true))
            assertTrue(item.noteUr.contains("تصدیق"))
            assertTrue(item.noteRoman.contains("tasdeeq", ignoreCase = true))
        }

        sos.logIncident(
            SosIncident(
                timestamp = "27 Nov, 12:00",
                locationText = "Location unavailable",
                batteryPercent = 50,
                messagePreview = "Test preview",
                recipientCount = 1,
                status = "Prepared"
            )
        )
        assertEquals(1, sos.getIncidents().size)
        sos.clearAll()
        assertTrue(sos.getContacts().isEmpty())
        assertTrue(sos.getIncidents().isEmpty())
    }

    @Test
    fun `lawyer marketplace filters by city category language and search query`() = runTest {
        val service = MockLawyerService()
        val all = service.getLawyers()
        assertEquals(4, all.size)
        assertTrue(all.all { it.isDemo })

        val multan = service.getLawyers(city = "Multan")
        assertEquals(1, multan.size)
        assertEquals("demo_lawyer_1", multan.first().id)

        val cyber = service.getLawyers(specialization = LegalCategory.CYBERCRIME)
        assertEquals(1, cyber.size)
        assertEquals("demo_lawyer_3", cyber.first().id)

        val urLawyers = service.getLawyers(language = AppLanguage.UR)
        assertEquals(3, urLawyers.size)
        assertFalse(urLawyers.any { it.id == "demo_lawyer_3" })

        val queryMatch = service.getLawyers(query = "bail")
        assertEquals(1, queryMatch.size)
        assertEquals("demo_lawyer_2", queryMatch.first().id)
    }

    @Test
    fun `lawyer demo review requires prior booking and replies include demo disclaimer`() = runTest {
        val service = MockLawyerService()
        val lawyer = service.getLawyerById("demo_lawyer_4")
        assertNotNull(lawyer)
        lawyer!!

        val blocked = service.addDemoReview(lawyer.id, "Sara", 5, "Helpful consultation")
        assertTrue(blocked.isFailure)
        assertEquals("BOOKING_REQUIRED", blocked.exceptionOrNull()?.message)

        val briefing = LawyerBriefingPack(
            summary = "Tenancy notice dispute in Islamabad",
            timeline = listOf(BriefingTimelineEvent("10:00", "Received notice")),
            peopleInvolved = listOf("Landlord"),
            evidenceChecklist = listOf("Rent agreement"),
            questionsForLawyer = listOf("What notice period applies?"),
            urgency = UrgencyLevel.MEDIUM,
            createdAt = "27 Nov"
        )
        val booking = service.bookConsultation(
            lawyer = lawyer,
            dateLabel = "Tomorrow",
            timeSlot = "11:00 AM",
            topic = "Tenancy",
            briefing = briefing
        )
        assertNotNull(booking.attachedBriefingTitle)
        assertEquals(1, service.getBookings().size)

        val allowed = service.addDemoReview(lawyer.id, "Sara", 5, "Helpful demo flow")
        assertTrue(allowed.isSuccess)
        val refreshed = service.getLawyerById(lawyer.id)
        assertNotNull(refreshed)
        assertEquals(1, refreshed!!.reviews.size)

        val enReply = service.sendDemoLawyerReply(lawyer, "Hello", AppLanguage.EN)
        val urReply = service.sendDemoLawyerReply(lawyer, "Salam", AppLanguage.UR)
        val romanReply = service.sendDemoLawyerReply(lawyer, "Salam", AppLanguage.ROMAN_UR)
        assertTrue(enReply.contains("Demo Account - Not a Real Lawyer"))
        assertTrue(urReply.contains("ڈیمو اکاؤنٹ"))
        assertTrue(romanReply.contains("Demo Account - Not a Real Lawyer"))
    }

    @Test
    fun `safety check-in state tracks inactive defaults and active timer`() {
        val defaultState = SafetyCheckInState()
        assertFalse(defaultState.isActive)
        assertEquals(15, defaultState.durationMinutes)
        assertEquals(0, defaultState.remainingSeconds)
        assertFalse(defaultState.expiredTriggered)

        val activeState = SafetyCheckInState(
            isActive = true,
            durationMinutes = 30,
            remainingSeconds = 1800,
            expiredTriggered = false
        )
        assertTrue(activeState.isActive)
        assertEquals(1800, activeState.remainingSeconds)
    }
}
