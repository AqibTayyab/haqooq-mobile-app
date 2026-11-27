package com.example.services.lawyers

import android.content.Context
import com.example.contracts.AppLanguage
import com.example.contracts.Lawyer
import com.example.contracts.LawyerBooking
import com.example.contracts.LawyerBriefingPack
import com.example.contracts.LawyerReview
import com.example.contracts.LawyerService
import com.example.contracts.LegalCategory
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Member 4 Lawyer Marketplace implementation backed by `assets/mock/lawyers.json`
 * with local persistence for demo bookings and post-booking demo reviews.
 */
class JsonLawyerService(context: Context) : LawyerService {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val baseLawyers: List<Lawyer> by lazy { loadLawyersFromAsset() }

    override suspend fun getLawyers(
        city: String?,
        specialization: LegalCategory?,
        language: AppLanguage?,
        query: String
    ): List<Lawyer> {
        val extraReviews = loadCustomReviewsMap()
        val q = query.trim().lowercase()
        return baseLawyers.map { lawyer ->
            val added = extraReviews[lawyer.id].orEmpty()
            if (added.isEmpty()) lawyer
            else {
                val allReviews = added + lawyer.reviews
                val avg = allReviews.map { it.rating }.average()
                val rounded = (avg * 10.0).roundToInt() / 10.0
                lawyer.copy(
                    reviews = allReviews,
                    reviewCount = allReviews.size,
                    rating = rounded
                )
            }
        }.filter { lawyer ->
            val matchesCity = city.isNullOrBlank() || lawyer.city.equals(city, ignoreCase = true)
            val matchesSpec = specialization == null ||
                specialization == LegalCategory.ALL ||
                specialization in lawyer.specializations
            val matchesLang = language == null || language in lawyer.languages
            val matchesQuery = q.isEmpty() ||
                lawyer.name.lowercase().contains(q) ||
                lawyer.city.lowercase().contains(q) ||
                lawyer.bioEn.lowercase().contains(q) ||
                lawyer.bioRoman.lowercase().contains(q) ||
                lawyer.bioUr.lowercase().contains(q) ||
                lawyer.specializations.any { it.id.contains(q) }
            matchesCity && matchesSpec && matchesLang && matchesQuery
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
        delay(220L)
        val booking = LawyerBooking(
            lawyerId = lawyer.id,
            lawyerName = lawyer.name,
            dateLabel = dateLabel,
            timeSlot = timeSlot,
            topic = topic.ifBlank { "General Consultation (Demo)" },
            status = "Confirmed (Demo)",
            attachedBriefingTitle = briefing?.summary?.take(48)
        )
        val list = loadBookings().toMutableList()
        list.add(0, booking)
        saveBookings(list)
        return booking
    }

    override suspend fun getBookings(): List<LawyerBooking> = loadBookings()

    override suspend fun addDemoReview(
        lawyerId: String,
        authorName: String,
        rating: Int,
        comment: String
    ): Result<LawyerReview> {
        val hasBooking = loadBookings().any { it.lawyerId == lawyerId }
        if (!hasBooking) {
            return Result.failure(IllegalStateException("BOOKING_REQUIRED"))
        }
        val cleanComment = comment.trim()
        if (cleanComment.isEmpty()) {
            return Result.failure(IllegalArgumentException("EMPTY_COMMENT"))
        }
        val safeAuthor = authorName.trim().ifBlank { "Citizen" }
            .let { if (it.contains("Demo", ignoreCase = true)) it else "$it (Demo)" }
        val review = LawyerReview(
            id = "rev_${UUID.randomUUID().toString().take(8)}",
            authorName = safeAuthor,
            rating = rating.coerceIn(1, 5),
            comment = cleanComment,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        )
        val map = loadCustomReviewsMap().toMutableMap()
        val updatedList = listOf(review) + map[lawyerId].orEmpty()
        map[lawyerId] = updatedList
        saveCustomReviewsMap(map)
        return Result.success(review)
    }

    override suspend fun clearAll() {
        prefs.edit().clear().apply()
    }

    override suspend fun sendDemoLawyerReply(
        lawyer: Lawyer,
        userMessage: String,
        language: AppLanguage
    ): String {
        delay(550L)
        val msgLower = userMessage.lowercase()
        val isBriefing = msgLower.contains("briefing") || userMessage.isBlank()
        val isUrgent = listOf("arrest", "police", "fir", "harass", "thana", "giraftar", "پولیس")
            .any { msgLower.contains(it) }

        return when (language) {
            AppLanguage.EN -> when {
                isBriefing -> "[Demo Account - Not a Real Lawyer] Thank you for attaching your Lawyer Briefing Pack. I can see your timeline and evidence checklist. In the live release, ${lawyer.name.removeSuffix(" (Demo)")} in ${lawyer.city} would verify documents and jurisdictional filings with you."
                isUrgent -> "[Demo Account - Not a Real Lawyer] Because this sounds time-sensitive, write down station/officer details and keep a family member informed. Use the SOS tab if you are in danger. A licensed advocate in ${lawyer.city} handles urgent bail/22-A petitions."
                else -> "[Demo Account - Not a Real Lawyer] Thank you for your message. Keep dated screenshots, CNIC copies, and written receipts organized before your consultation in ${lawyer.city}."
            }
            AppLanguage.UR -> when {
                isBriefing -> "[ڈیمو اکاؤنٹ — حقیقی وکیل نہیں] بریفنگ پیک شیئر کرنے کا شکریہ۔ آپ کی ٹائم لائن اور شواہد کی فہرست موصول ہو گئی ہے۔ مکمل ورژن میں ${lawyer.city} کے مستند وکیل آپ کے کاغذات کا جائزہ لیں گے۔"
                isUrgent -> "[ڈیمو اکاؤنٹ — حقیقی وکیل نہیں] یہ معاملہ فوری توجہ کا متقاضی لگتا ہے۔ تھانے یا افسر کی تفصیل محفوظ رکھیں اور خطرے کی صورت میں SOS استعمال کریں۔"
                else -> "[ڈیمو اکاؤنٹ — حقیقی وکیل نہیں] پیغام کا شکریہ۔ براہِ کرم تمام اسکرین شاٹس، رسیدیں اور شناختی کارڈ کی کاپی تاریخ کے ساتھ محفوظ رکھیں۔"
            }
            AppLanguage.ROMAN_UR -> when {
                isBriefing -> "[Demo Account - Not a Real Lawyer] Briefing Pack attach karne ka shukriya. Aapki timeline aur saboot ki checklist mil gayi hai. Mukammal app mein ${lawyer.city} ke wakeel aapke kaghzat check karte hain."
                isUrgent -> "[Demo Account - Not a Real Lawyer] Yeh masla fori lagta hai. Thana/officer ki tafseel likh lein, ghar walon ko ittila dein, aur khatre mein SOS tab use karein."
                else -> "[Demo Account - Not a Real Lawyer] Message ka shukriya. Apne screenshots, raseedein aur CNIC copy tareekh ke saath mehfooz rakhein."
            }
        }
    }

    private fun loadLawyersFromAsset(): List<Lawyer> {
        return runCatching {
            val jsonStr = appContext.assets.open("mock/lawyers.json")
                .bufferedReader()
                .use { it.readText() }
            val arr = JSONArray(jsonStr)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val specArr = obj.getJSONArray("specializations")
                    val specs = buildList {
                        for (j in 0 until specArr.length()) {
                            val id = specArr.getString(j)
                            LegalCategory.entries.firstOrNull { it.id == id }?.let { add(it) }
                        }
                    }
                    val langArr = obj.getJSONArray("languages")
                    val langs = buildList {
                        for (j in 0 until langArr.length()) {
                            add(AppLanguage.fromCode(langArr.getString(j)))
                        }
                    }
                    val revArr = obj.optJSONArray("reviews") ?: JSONArray()
                    val reviews = buildList {
                        for (j in 0 until revArr.length()) {
                            val r = revArr.getJSONObject(j)
                            add(
                                LawyerReview(
                                    id = r.getString("id"),
                                    authorName = r.getString("author_name"),
                                    rating = r.getInt("rating"),
                                    comment = r.getString("comment"),
                                    date = r.getString("date")
                                )
                            )
                        }
                    }
                    add(
                        Lawyer(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            city = obj.getString("city"),
                            specializations = specs,
                            languages = langs,
                            experienceYears = obj.getInt("experience_years"),
                            feePkr = obj.getString("fee_pkr"),
                            rating = obj.getDouble("rating"),
                            reviewCount = obj.optInt("review_count", reviews.size),
                            bioEn = obj.getString("bio_en"),
                            bioUr = obj.getString("bio_ur"),
                            bioRoman = obj.getString("bio_roman"),
                            isDemo = obj.optBoolean("is_demo", true),
                            reviews = reviews
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun loadBookings(): List<LawyerBooking> {
        val raw = prefs.getString(KEY_BOOKINGS_JSON, "[]") ?: "[]"
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        LawyerBooking(
                            id = o.getString("id"),
                            lawyerId = o.getString("lawyerId"),
                            lawyerName = o.getString("lawyerName"),
                            dateLabel = o.getString("dateLabel"),
                            timeSlot = o.getString("timeSlot"),
                            topic = o.getString("topic"),
                            status = o.getString("status"),
                            attachedBriefingTitle = o.optString("attachedBriefingTitle").ifBlank { null }
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveBookings(bookings: List<LawyerBooking>) {
        val arr = JSONArray()
        bookings.forEach { b ->
            arr.put(
                JSONObject().apply {
                    put("id", b.id)
                    put("lawyerId", b.lawyerId)
                    put("lawyerName", b.lawyerName)
                    put("dateLabel", b.dateLabel)
                    put("timeSlot", b.timeSlot)
                    put("topic", b.topic)
                    put("status", b.status)
                    put("attachedBriefingTitle", b.attachedBriefingTitle ?: "")
                }
            )
        }
        prefs.edit().putString(KEY_BOOKINGS_JSON, arr.toString()).apply()
    }

    private fun loadCustomReviewsMap(): Map<String, List<LawyerReview>> {
        val raw = prefs.getString(KEY_REVIEWS_JSON, "{}") ?: "{}"
        return runCatching {
            val root = JSONObject(raw)
            val result = mutableMapOf<String, List<LawyerReview>>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val lawyerId = keys.next()
                val arr = root.getJSONArray(lawyerId)
                val list = buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        add(
                            LawyerReview(
                                id = o.getString("id"),
                                authorName = o.getString("authorName"),
                                rating = o.getInt("rating"),
                                comment = o.getString("comment"),
                                date = o.getString("date")
                            )
                        )
                    }
                }
                result[lawyerId] = list
            }
            result
        }.getOrDefault(emptyMap())
    }

    private fun saveCustomReviewsMap(map: Map<String, List<LawyerReview>>) {
        val root = JSONObject()
        map.forEach { (lawyerId, list) ->
            val arr = JSONArray()
            list.forEach { r ->
                arr.put(
                    JSONObject().apply {
                        put("id", r.id)
                        put("authorName", r.authorName)
                        put("rating", r.rating)
                        put("comment", r.comment)
                        put("date", r.date)
                    }
                )
            }
            root.put(lawyerId, arr)
        }
        prefs.edit().putString(KEY_REVIEWS_JSON, root.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "haqooq_lawyers_store"
        private const val KEY_BOOKINGS_JSON = "demo_bookings_json"
        private const val KEY_REVIEWS_JSON = "demo_reviews_json"
    }
}
