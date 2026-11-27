package com.example.services.sos

import android.content.Context
import com.example.contracts.AppLanguage
import com.example.contracts.EmergencyContactItem
import com.example.contracts.SosIncident
import com.example.contracts.SosService
import com.example.contracts.TrustedContact
import org.json.JSONArray
import org.json.JSONObject

/**
 * Member 4 persistent SOS repository implementing [SosService].
 * Stores trusted contacts (max 5) and SOS incident history locally across app restarts.
 */
class PersistentSosService(context: Context) : SosService {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        if (!prefs.getBoolean(KEY_SEEDED, false)) {
            val defaultContacts = listOf(
                TrustedContact(
                    id = "tc_1",
                    name = "Hamza Tariq (Demo)",
                    phone = "03001234567",
                    relationship = "Brother",
                    isPrimary = true
                )
            )
            saveContacts(defaultContacts)
            prefs.edit().putBoolean(KEY_SEEDED, true).apply()
        }
    }

    override suspend fun getContacts(): List<TrustedContact> = loadContacts()

    override suspend fun addContact(contact: TrustedContact): Result<TrustedContact> {
        val list = loadContacts().toMutableList()
        if (list.size >= 5) {
            return Result.failure(IllegalStateException("MAX_CONTACTS"))
        }
        if (!isValidPakistaniPhone(contact.phone)) {
            return Result.failure(IllegalArgumentException("INVALID_PHONE"))
        }
        val normalized = contact.copy(phone = normalizePhone(contact.phone))
        if (normalized.isPrimary) {
            for (i in list.indices) {
                list[i] = list[i].copy(isPrimary = false)
            }
        }
        list.add(normalized)
        saveContacts(list)
        return Result.success(normalized)
    }

    override suspend fun updateContact(contact: TrustedContact): Result<TrustedContact> {
        if (!isValidPakistaniPhone(contact.phone)) {
            return Result.failure(IllegalArgumentException("INVALID_PHONE"))
        }
        val list = loadContacts().toMutableList()
        val idx = list.indexOfFirst { it.id == contact.id }
        if (idx == -1) {
            return Result.failure(IllegalArgumentException("NOT_FOUND"))
        }
        val normalized = contact.copy(phone = normalizePhone(contact.phone))
        if (normalized.isPrimary) {
            for (i in list.indices) {
                list[i] = list[i].copy(isPrimary = false)
            }
        }
        list[idx] = normalized
        saveContacts(list)
        return Result.success(normalized)
    }

    override suspend fun removeContact(contactId: String) {
        val list = loadContacts().filterNot { it.id == contactId }
        saveContacts(list)
    }

    override suspend fun buildSosAlertMessage(
        language: AppLanguage,
        locationText: String?,
        batteryPercent: Int
    ): String {
        val locPart = locationText ?: when (language) {
            AppLanguage.EN -> "Location unavailable (GPS off or permission denied)"
            AppLanguage.UR -> "لوکیشن دستیاب نہیں (GPS بند یا اجازت نہیں)"
            AppLanguage.ROMAN_UR -> "Location dastiyab nahi (GPS band ya ijazat nahi)"
        }
        return when (language) {
            AppLanguage.EN -> "EMERGENCY SOS (HAQOOQ): I may need urgent help. Location: $locPart | Battery: $batteryPercent%. Please call or reach out immediately."
            AppLanguage.UR -> "ایمرجنسی SOS (حقوق): مجھے فوری مدد کی ضرورت ہے۔ لوکیشن: $locPart | بیٹری: $batteryPercent%۔ براہِ کرم فوراً رابطہ کریں۔"
            AppLanguage.ROMAN_UR -> "EMERGENCY SOS (HAQOOQ): Mujhe fori madad ki zaroorat hai. Location: $locPart | Battery: $batteryPercent%. Barah-e-karam foran call karein."
        }
    }

    override suspend fun logIncident(incident: SosIncident) {
        val list = loadIncidents().toMutableList()
        list.add(0, incident)
        saveIncidents(list.take(30))
    }

    override suspend fun getIncidents(): List<SosIncident> = loadIncidents()

    override suspend fun clearAll() {
        prefs.edit()
            .putString(KEY_CONTACTS_JSON, "[]")
            .putString(KEY_INCIDENTS_JSON, "[]")
            .putBoolean(KEY_SEEDED, true)
            .apply()
    }

    override fun getEmergencyNumbers(): List<EmergencyContactItem> = listOf(
        EmergencyContactItem(
            id = "em_police",
            labelEn = "Police Emergency",
            labelUr = "پولیس ایمرجنسی",
            labelRoman = "Police Emergency",
            number = "15",
            noteEn = "Widely published Police helpline (VERIFY locally)",
            noteUr = "سرکاری پولیس ہیلپ لائن (مقامی تصدیق کریں)",
            noteRoman = "Sarkari Police helpline (tasdeeq karein)"
        ),
        EmergencyContactItem(
            id = "em_rescue",
            labelEn = "Rescue / Ambulance",
            labelUr = "ریسکیو / ایمبولینس",
            labelRoman = "Rescue / Ambulance",
            number = "1122",
            noteEn = "Medical & fire rescue service (VERIFY locally)",
            noteUr = "طبی اور ریسکیو سروس (مقامی تصدیق کریں)",
            noteRoman = "Medical aur rescue service (tasdeeq karein)"
        ),
        EmergencyContactItem(
            id = "em_mohr",
            labelEn = "Human Rights / Legal Aid Helpline",
            labelUr = "انسانی حقوق / قانونی مدد ہیلپ لائن",
            labelRoman = "Insani Haqooq / Legal Aid",
            number = "1099",
            noteEn = "Ministry of Human Rights helpline (VERIFY locally)",
            noteUr = "وزارتِ انسانی حقوق ہیلپ لائن (تصدیق کریں)",
            noteRoman = "Ministry of Human Rights helpline (tasdeeq karein)"
        ),
        EmergencyContactItem(
            id = "em_cyber",
            labelEn = "Cyber Crime Helpline",
            labelUr = "سائبر کرائم ہیلپ لائن",
            labelRoman = "Cyber Crime Helpline",
            number = "1991",
            noteEn = "Cybercrime reporting desk (VERIFY locally)",
            noteUr = "سائبر کرائم شکایت ڈیسک (تصدیق کریں)",
            noteRoman = "Cybercrime reporting desk (tasdeeq karein)"
        )
    )

    override fun isValidPakistaniPhone(phone: String): Boolean {
        val cleaned = normalizePhone(phone)
        return Regex("^(\\+92|0)3\\d{9}$").matches(cleaned)
    }

    private fun normalizePhone(phone: String): String =
        phone.replace(Regex("[\\s-]"), "")

    private fun loadContacts(): List<TrustedContact> {
        val raw = prefs.getString(KEY_CONTACTS_JSON, "[]") ?: "[]"
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        TrustedContact(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            phone = obj.getString("phone"),
                            relationship = obj.optString("relationship", "Family"),
                            isPrimary = obj.optBoolean("isPrimary", false)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveContacts(contacts: List<TrustedContact>) {
        val arr = JSONArray()
        contacts.forEach { c ->
            arr.put(
                JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("phone", c.phone)
                    put("relationship", c.relationship)
                    put("isPrimary", c.isPrimary)
                }
            )
        }
        prefs.edit().putString(KEY_CONTACTS_JSON, arr.toString()).apply()
    }

    private fun loadIncidents(): List<SosIncident> {
        val raw = prefs.getString(KEY_INCIDENTS_JSON, "[]") ?: "[]"
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        SosIncident(
                            id = obj.getString("id"),
                            timestamp = obj.getString("timestamp"),
                            locationText = obj.getString("locationText"),
                            batteryPercent = obj.optInt("batteryPercent", 0),
                            messagePreview = obj.getString("messagePreview"),
                            recipientCount = obj.optInt("recipientCount", 0),
                            status = obj.getString("status")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveIncidents(incidents: List<SosIncident>) {
        val arr = JSONArray()
        incidents.forEach { inc ->
            arr.put(
                JSONObject().apply {
                    put("id", inc.id)
                    put("timestamp", inc.timestamp)
                    put("locationText", inc.locationText)
                    put("batteryPercent", inc.batteryPercent)
                    put("messagePreview", inc.messagePreview)
                    put("recipientCount", inc.recipientCount)
                    put("status", inc.status)
                }
            )
        }
        prefs.edit().putString(KEY_INCIDENTS_JSON, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "haqooq_sos_store"
        private const val KEY_SEEDED = "contacts_seeded"
        private const val KEY_CONTACTS_JSON = "trusted_contacts_json"
        private const val KEY_INCIDENTS_JSON = "sos_incidents_json"
    }
}
