package com.example.data

import com.example.contracts.AppLanguage
import com.example.contracts.LegalCard
import com.example.contracts.LegalCategory
import com.example.contracts.UrgencyLevel

/**
 * Plain-language offline rights cards covering starter topics in English, Urdu, and Roman Urdu.
 * No invented sections or punishments; official portal links provided for verification.
 */
object LegalCardsData {

    val allCards: List<LegalCard> = listOf(
        // 1. Police Stop (EN / UR / ROMAN_UR)
        LegalCard(
            id = "police_stop_001",
            title = "If Police Stop You on the Road",
            category = LegalCategory.POLICE_ARREST,
            lang = AppLanguage.EN,
            body = "Stay calm and polite. You may ask why you are being stopped and request to see the officer's identification. Keep your original CNIC, driving license, and vehicle documents ready. Avoid arguments and do not offer bribes.",
            steps = listOf(
                "Stop safely and keep both hands visible",
                "Politely ask the officer's name, belt number, and reason for stopping",
                "Show your CNIC, license, and vehicle registration",
                "Call a trusted family member if you feel unsafe or detained"
            ),
            sourceName = "Constitution of Pakistan (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("police", "stop", "checkpoint", "nakabandi", "license", "cnic", "roka"),
            isDraft = true
        ),
        LegalCard(
            id = "police_stop_001_ur",
            title = "اگر پولیس آپ کو سڑک پر روکے",
            category = LegalCategory.POLICE_ARREST,
            lang = AppLanguage.UR,
            body = "پُرسکون اور شائستہ رہیں۔ آپ روکنے کی وجہ پوچھ سکتے ہیں اور افسر کا شناختی کارڈ دیکھنے کی درخواست کر سکتے ہیں۔ اپنا اصل شناختی کارڈ، ڈرائیونگ لائسنس اور گاڑی کے کاغذات دکھائیں۔ بحث یا رشوت سے گریز کریں۔",
            steps = listOf(
                "محفوظ جگہ پر گاڑی روکیں اور پُرسکون رہیں",
                "شائستگی سے افسر کا نام، بیلٹ نمبر اور روکنے کی وجہ پوچھیں",
                "اپنا شناختی کارڈ، لائسنس اور رجسٹریشن دکھائیں",
                "غیر محفوظ محسوس کریں تو فوراً گھر والوں یا وکیل کو اطلاع دیں"
            ),
            sourceName = "Constitution of Pakistan (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("پولیس", "ناکہ", "چیک پوسٹ", "شناختی کارڈ", "police", "stop"),
            isDraft = true
        ),
        LegalCard(
            id = "police_stop_001_ru",
            title = "Agar Police Road Par Roke",
            category = LegalCategory.POLICE_ARREST,
            lang = AppLanguage.ROMAN_UR,
            body = "Pur-sukoon aur tameez se baat karein. Aap rokne ki wajah pooch sakte hain aur officer ki shanakht dekhne ki darkhwast kar sakte hain. Apna asal CNIC, driving license aur gaari ke kaghzat paas rakhein. Behas ya rishwat se gurez karein.",
            steps = listOf(
                "Mehfooz jagah gaari rokein aur pur-sukoon rahein",
                "Shayistagi se officer ka naam, belt number aur rokne ki wajah poochein",
                "Apna CNIC, license aur registration dikhayein",
                "Ghair-mehfooz mehsoos ho to foran ghar walon ya wakeel ko call karein"
            ),
            sourceName = "Constitution of Pakistan (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("police", "roka", "naka", "license", "cnic", "thana", "stop"),
            isDraft = true
        ),

        // 2. Arrest & Detention
        LegalCard(
            id = "police_arrest_001",
            title = "Safeguards If Arrested or Detained",
            category = LegalCategory.POLICE_ARREST,
            lang = AppLanguage.EN,
            body = "Under Pakistan's constitutional safeguards, a person who is arrested has the right to be informed of the grounds of arrest, to consult and be defended by a lawyer of choice, and to be produced before a magistrate within the legally prescribed period (exceptions may apply under specific preventive laws).",
            steps = listOf(
                "Ask clearly for the reason for arrest and whether there is a warrant",
                "Request to inform a family member and contact a lawyer of your choice",
                "Do not sign blank papers or statements you have not read or understood",
                "Note the officers' names, police station (thana), date, and time"
            ),
            sourceName = "Constitution of Pakistan (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("arrest", "detain", "giraftar", "thana", "warrant", "magistrate", "police"),
            isDraft = true
        ),
        LegalCard(
            id = "police_arrest_001_ur",
            title = "گرفتاری یا حراست کی صورت میں بنیادی حقوق",
            category = LegalCategory.POLICE_ARREST,
            lang = AppLanguage.UR,
            body = "آئینِ پاکستان کے بنیادی حقوق کے تحت گرفتار شخص کو گرفتاری کی وجہ جاننے، اپنی پسند کے وکیل سے مشورہ کرنے، اور مقررہ مدت کے اندر مجسٹریٹ کے سامنے پیش کیے جانے کا حق حاصل ہے (خصوصی قوانین میں استثناء ہو سکتا ہے)۔",
            steps = listOf(
                "گرفتاری کی وجہ اور وارنٹ کے بارے میں واضح طور پر پوچھیں",
                "اہلِ خانہ کو اطلاع دینے اور وکیل سے رابطہ کرنے کا مطالبہ کریں",
                "بغیر پڑھے یا سمجھے کسی سادہ یا تحریری کاغذ پر دستخط نہ کریں",
                "افسران کے نام، تھانہ، تاریخ اور وقت یاد رکھیں"
            ),
            sourceName = "Constitution of Pakistan (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("گرفتار", "حراست", "تھانہ", "وارنٹ", "وکیل", "arrest", "police"),
            isDraft = true
        ),
        LegalCard(
            id = "police_arrest_001_ru",
            title = "Giraftari ya Hirasat Mein Aapke Haqooq",
            category = LegalCategory.POLICE_ARREST,
            lang = AppLanguage.ROMAN_UR,
            body = "Aain-e-Pakistan ke bunyadi haqooq ke mutabiq giraftar shakhs ko giraftari ki wajah jaanne, apni pasand ke wakeel se mashwara karne, aur muqarrara muddat mein magistrate ke samne pesh kiye jaane ka haq hota hai (khas qawaneen mein istisna ho sakta hai).",
            steps = listOf(
                "Saaf poochein ke giraftari ki wajah kya hai aur kya warrant maujood hai",
                "Ghar walon ko ittila dene aur wakeel se baat karne ki darkhwast karein",
                "Bina parhe ya samjhe kisi khali ya likhe kagaz par dastakhat na karein",
                "Officers ke naam, thana, tareekh aur waqt yaad rakhein"
            ),
            sourceName = "Constitution of Pakistan (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("giraftar", "hirasat", "arrest", "thana", "warrant", "magistrate", "police"),
            isDraft = true
        ),

        // 3. FIR Registration & Refusal
        LegalCard(
            id = "fir_001",
            title = "How to Register an FIR (and If Police Refuse)",
            category = LegalCategory.FIR,
            lang = AppLanguage.EN,
            body = "When a serious (cognizable) offence occurs, the local police station is expected to record your information in an FIR and give you a free copy. If the police station refuses or delays, you may submit a written application to senior police officers or seek an order through the Justice of Peace / Sessions Court with a lawyer's help.",
            steps = listOf(
                "Write a clear complaint with date, time, place, names, and facts",
                "Submit it to the local SHO and ask for a signed receiving/diary number",
                "If refused, escalate in writing to the SP/DPO Complaints Cell or 15/1787",
                "Consult a lawyer about filing a petition before the Ex-Officio Justice of Peace"
            ),
            sourceName = "Code of Criminal Procedure & Police Portals (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("fir", "report", "sho", "refuse", "thana", "complaint", "22a"),
            isDraft = true
        ),
        LegalCard(
            id = "fir_001_ur",
            title = "ایف آئی آر درج کرانا اور انکار کی صورت میں حل",
            category = LegalCategory.FIR,
            lang = AppLanguage.UR,
            body = "قابلِ دست اندازی جرم کی اطلاع پر متعلقہ تھانہ آپ کا بیان ایف آئی آر میں درج کر کے مفت نقل فراہم کرنے کا پابند ہے۔ اگر تھانہ انکار یا تاخیر کرے تو آپ اعلیٰ پولیس افسران کو تحریری درخواست دے سکتے ہیں یا وکیل کے ذریعے جسٹس آف پیس / سیشن کورٹ سے رجوع کر سکتے ہیں۔",
            steps = listOf(
                "واقعے کی تاریخ، وقت، جگہ اور مکمل تفصیل تحریری درخواست میں لکھیں",
                "متعلقہ ایس ایچ او کو جمع کروائیں اور روزنامچہ نمبر یا رسید لیں",
                "انکار کی صورت میں ایس پی / ڈی پی او کمپلینٹ سیل یا 15/1787 پر شکایت کریں",
                "عدالت میں درخواست کے لیے کسی وکیل سے مشورہ کریں"
            ),
            sourceName = "Code of Criminal Procedure (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("ایف آئی آر", "تھانہ", "شکایت", "انکار", "fir", "report"),
            isDraft = true
        ),
        LegalCard(
            id = "fir_001_ru",
            title = "FIR Darj Karwana aur Inkar par Hal",
            category = LegalCategory.FIR,
            lang = AppLanguage.ROMAN_UR,
            body = "Kisi bare jurm ki surat mein mutaliqa thana aapki ittila par FIR darj karke muft copy dene ka paband hota hai. Agar thana inkar kare ya der lagaye to aap senior police officer (SP/DPO) ko likhi darkhwast de sakte hain ya wakeel ke zariye Justice of Peace / Sessions Court se ruju kar sakte hain.",
            steps = listOf(
                "Waqia ki tareekh, waqt, jagah aur tafseel saaf kagaz par likhein",
                "Mutaliqa SHO ko jama karwayein aur signed receiving / diary number lein",
                "Inkar par SP/DPO Complaints Cell ya 15/1787 par shikayat darj karein",
                "Court petition ke liye kisi mustanad wakeel se mashwara karein"
            ),
            sourceName = "Code of Criminal Procedure (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("fir", "inkar", "refuse", "sho", "thana", "report", "darj"),
            isDraft = true
        ),

        // 4. Bail Basics
        LegalCard(
            id = "bail_001",
            title = "Bail Basics (Pre-Arrest & Post-Arrest)",
            category = LegalCategory.BAIL_COURT,
            lang = AppLanguage.EN,
            body = "Bail allows an accused person temporary release while a case proceeds, depending on whether the alleged offence is bailable or non-bailable. A lawyer can apply for pre-arrest (protective/anticipatory) bail if arrest is feared, or post-arrest bail after arrest.",
            steps = listOf(
                "Obtain a copy of the FIR or case details immediately",
                "Contact a criminal defence lawyer to evaluate bailable vs non-bailable status",
                "Arrange surety documents (ID card and surety bond details) if required by court",
                "Attend every court hearing on time to avoid bail cancellation"
            ),
            sourceName = "Code of Criminal Procedure (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("bail", "zamanat", "court", "surety", "pre-arrest"),
            isDraft = true
        ),
        LegalCard(
            id = "bail_001_ur",
            title = "ضمانت کے بنیادی اصول (قبل از گرفتاری اور بعد از گرفتاری)",
            category = LegalCategory.BAIL_COURT,
            lang = AppLanguage.UR,
            body = "ضمانت کے ذریعے مقدمے کی سماعت کے دوران ملزم کی عارضی رہائی ممکن ہو سکتی ہے، جس کا انحصار جرم کی نوعیت پر ہوتا ہے۔ گرفتاری کے خدشے پر قبل از گرفتاری اور گرفتاری کے بعد بعد از گرفتاری ضمانت کی درخواست وکیل کے ذریعے دی جاتی ہے۔",
            steps = listOf(
                "فوراً ایف آئی آر کی نقل یا مقدمے کی تفصیلات حاصل کریں",
                "مقدمے کی نوعیت جانچنے کے لیے فوجداری وکیل سے رابطہ کریں",
                "عدالت کے طلب کرنے پر ضامن اور شناختی کاغذات تیار رکھیں",
                "ضمانت منسوخی سے بچنے کے لیے ہر پیشی پر وقت پر حاضر ہوں"
            ),
            sourceName = "Code of Criminal Procedure (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("ضمانت", "عدالت", "bail", "zamanat"),
            isDraft = true
        ),
        LegalCard(
            id = "bail_001_ru",
            title = "Zamanat (Bail) ke Bunyadi Usool",
            category = LegalCategory.BAIL_COURT,
            lang = AppLanguage.ROMAN_UR,
            body = "Zamanat ke zariye muqadme ke dauran mulzim ki aarzi rihai ho sakti hai, jo jurm ki noiyat par munhasir hai. Giraftari ke khatre mein wakeel ke zariye qabal az giraftari (pre-arrest) ya baad az giraftari zamanat ki darkhwast di ja sakti hai.",
            steps = listOf(
                "Foran FIR ki copy ya case ki tafseel hasil karein",
                "Bailable ya non-bailable noiyat samajhne ke liye wakeel se raabta karein",
                "Adalat ke kehne par zamin (surety) ke kaghzat tayar rakhein",
                "Har tareekh par waqt par adalat mein hazir hon"
            ),
            sourceName = "Code of Criminal Procedure (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("bail", "zamanat", "court", "adalat", "surety"),
            isDraft = true
        ),

        // 5. Cyber Harassment & Online Fraud
        LegalCard(
            id = "cyber_001",
            title = "Online Harassment, Blackmail & Hacked Accounts",
            category = LegalCategory.CYBERCRIME,
            lang = AppLanguage.EN,
            body = "If someone blackmails you online, shares private photos without consent, impersonates you, or hacks your account, preserve digital evidence immediately before blocking. You may report to the FIA Cyber Crime Wing / National Cyber Crime Investigation Agency.",
            steps = listOf(
                "Take full screenshots showing URLs, usernames, dates, and messages",
                "Do NOT delete chats or transfer money to blackmailers",
                "Secure your remaining accounts (change passwords, enable 2FA)",
                "File a complaint on the official cybercrime portal or call 1991 (verify locally)"
            ),
            sourceName = "PECA / FIA Cyber Crime Portal",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("cyber", "hack", "blackmail", "fraud", "online", "scam", "photo", "fia"),
            isDraft = true
        ),
        LegalCard(
            id = "cyber_001_ur",
            title = "آن لائن ہراسانی، بلیک میلنگ اور اکاؤنٹ ہیکنگ",
            category = LegalCategory.CYBERCRIME,
            lang = AppLanguage.UR,
            body = "اگر کوئی آپ کو آن لائن بلیک میل کرے، اجازت کے بغیر ذاتی تصاویر شیئر کرے یا اکاؤنٹ ہیک کرے تو بلاک کرنے سے پہلے مکمل ثبوت محفوظ کریں۔ آپ سرکاری سائبر کرائم پورٹل یا ہیلپ لائن پر شکایت درج کر سکتے ہیں۔",
            steps = listOf(
                "یو آر ایل، یوزر نیم، تاریخ اور پیغامات کے مکمل اسکرین شاٹس لیں",
                "چیٹ حذف نہ کریں اور بلیک میلر کو پیسے ہرگز نہ بھیجیں",
                "باقی اکاؤنٹس کے پاس ورڈ بدلیں اور ٹو فیکٹر تصدیق آن کریں",
                "سرکاری سائبر کرائم پورٹل یا 1991 (مقامی تصدیق کے بعد) پر شکایت درج کریں"
            ),
            sourceName = "PECA / FIA Cyber Crime Portal",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("سائبر", "ہیک", "ہراسانی", "بلیک میل", "cyber", "hack", "fraud"),
            isDraft = true
        ),
        LegalCard(
            id = "cyber_001_ru",
            title = "Online Harassment, Blackmail aur Account Hack",
            category = LegalCategory.CYBERCRIME,
            lang = AppLanguage.ROMAN_UR,
            body = "Agar koi aapko online blackmail kare, ijazat ke baghair private tasaveer share kare ya account hack kare to block karne se pehle saboot mehfooz karein. Aap sarkari cybercrime portal ya helpline par shikayat kar sakte hain.",
            steps = listOf(
                "URL, username, tareekh aur messages ke poore screenshots save karein",
                "Chats delete na karein aur blackmailer ko paise na bhejein",
                "Baqi accounts ke password badlein aur 2FA on karein",
                "Sarkari cybercrime portal ya 1991 (tasdeeq ke baad) par complaint karein"
            ),
            sourceName = "PECA / FIA Cyber Crime Portal",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("cyber", "hack", "online", "harassment", "blackmail", "fraud", "scam"),
            isDraft = true
        ),

        // 6. Workplace & Street Harassment
        LegalCard(
            id = "women_001",
            title = "Workplace Harassment Protections",
            category = LegalCategory.WOMEN_SAFETY,
            lang = AppLanguage.EN,
            body = "Pakistani law protects employees against sexual harassment at the workplace. Organizations are expected to have an internal Inquiry Committee, and complainants may also approach the Federal or Provincial Ombudsperson against Harassment at the Workplace.",
            steps = listOf(
                "If safe, document dates, times, messages, emails, and any witnesses",
                "Report in writing to your organization's Inquiry Committee",
                "If no action is taken or committee is absent, contact the Ombudsperson office",
                "Seek legal aid or call women helpline 1043 / 1099 if in immediate distress"
            ),
            sourceName = "Protection Against Harassment of Women at the Workplace Act (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("harass", "workplace", "women", "ombudsperson", "office"),
            isDraft = true
        ),
        LegalCard(
            id = "women_001_ur",
            title = "کام کی جگہ پر ہراسانی سے تحفظ",
            category = LegalCategory.WOMEN_SAFETY,
            lang = AppLanguage.UR,
            body = "پاکستانی قانون کام کی جگہ پر ہراسانی کے خلاف تحفظ فراہم کرتا ہے۔ اداروں میں انکوائری کمیٹی کا ہونا ضروری ہے، اور متاثرہ شخص وفاقی یا صوبائی محتسب برائے انسدادِ ہراسانی سے بھی رجوع کر سکتا ہے۔",
            steps = listOf(
                "تاریخ، وقت، پیغامات، ای میلز اور گواہوں کی تفصیل محفوظ کریں",
                "ادارے کی انکوائری کمیٹی کو تحریری شکایت جمع کروائیں",
                "کمیٹی نہ ہونے یا کارروائی نہ ہونے پر محتسب آفس سے رابطہ کریں",
                "فوری مدد کے لیے خواتین ہیلپ لائن 1043 یا 1099 پر کال کریں"
            ),
            sourceName = "Workplace Harassment Act (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("ہراسانی", "خواتین", "دفتر", "harass", "women"),
            isDraft = true
        ),
        LegalCard(
            id = "women_001_ru",
            title = "Daftar / Kaam ki Jagah Harassment se Tahaffuz",
            category = LegalCategory.WOMEN_SAFETY,
            lang = AppLanguage.ROMAN_UR,
            body = "Pakistani qanoon kaam ki jagah par harassment ke khilaf tahaffuz deta hai. Idaron mein Inquiry Committee hona zaroori hai, aur aap Wafaqi ya Subai Ombudsperson (Mohtasib) ko bhi shikayat kar sakte hain.",
            steps = listOf(
                "Tareekh, waqt, messages, emails aur gawahon ka record mehfooz karein",
                "Idare ki Inquiry Committee ko likhi shikayat dein",
                "Kaarwai na hone par Ombudsperson office se raabta karein",
                "Fori madad ke liye 1043 ya 1099 helpline par call karein"
            ),
            sourceName = "Workplace Harassment Act (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("harass", "harassment", "women", "office", "workplace", "khawateen"),
            isDraft = true
        ),

        // 7. Domestic Violence Help
        LegalCard(
            id = "domestic_001",
            title = "Domestic Violence & Immediate Safety",
            category = LegalCategory.DOMESTIC,
            lang = AppLanguage.EN,
            body = "If you or someone with you faces physical, emotional, or economic abuse at home, personal safety comes first. Provincial domestic violence protection laws allow protection orders and shelter support through courts, police, and helplines.",
            steps = listOf(
                "Move to a safe room or trusted neighbour/relative's home if in danger",
                "Call Police 15, Rescue 1122, or Human Rights Helpline 1099",
                "Keep CNIC, children's documents, and medical reports safely accessible",
                "Speak with a family lawyer or legal-aid organization about protection orders"
            ),
            sourceName = "Provincial Domestic Violence Acts & MoHR Helpline",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.HIGH,
            keywords = listOf("domestic", "violence", "abuse", "ghar", "maar", "dhamki", "family"),
            isDraft = true
        ),

        // 8. Unpaid Salary / Employment
        LegalCard(
            id = "employment_001",
            title = "Unpaid Salary or Wrongful Dismissal",
            category = LegalCategory.EMPLOYMENT,
            lang = AppLanguage.EN,
            body = "If your employer withholds earned wages or terminates your employment unfairly, gather your contract, salary slips, bank transfers, and attendance logs. Depending on your role and province, you may file a claim before the Labour Court / Authority under Payment of Wages laws.",
            steps = listOf(
                "Collect appointment letter, bank statements, and WhatsApp/email work proof",
                "Send a polite written notice requesting overdue salary by a clear date",
                "Approach the provincial Labour Department or Authority under Payment of Wages",
                "Consult a labour lawyer before signing any full-and-final settlement paper"
            ),
            sourceName = "Payment of Wages & Labour Laws (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("salary", "wage", "employer", "naukri", "tankhwah", "job", "labour"),
            isDraft = true
        ),
        LegalCard(
            id = "employment_001_ur",
            title = "تنخواہ کی عدم ادائیگی یا غیر منصفانہ برطرفی",
            category = LegalCategory.EMPLOYMENT,
            lang = AppLanguage.UR,
            body = "اگر آجر آپ کی محنت کی تنخواہ روکے یا بغیر وجہ ملازمت سے نکالے تو معاہدہ، بینک اسٹیٹمنٹ اور حاضری کا ریکارڈ محفوظ کریں۔ آپ صوبائی لیبر ڈیپارٹمنٹ یا لیبر کورٹ میں دعویٰ دائر کر سکتے ہیں۔",
            steps = listOf(
                "ملازمت کا خط، بینک اسٹیٹمنٹ اور کام کے ثبوت اکٹھے کریں",
                "تحریری طور پر بقایا تنخواہ کی ادائیگی کی درخواست بھیجیں",
                "صوبائی لیبر ڈیپارٹمنٹ یا اتھارٹی سے رجوع کریں",
                "حتمی تصفیے کے کاغذات پر دستخط سے پہلے وکیل سے مشورہ کریں"
            ),
            sourceName = "Labour Laws (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("تنخواہ", "ملازمت", "نوکری", "salary", "job"),
            isDraft = true
        ),
        LegalCard(
            id = "employment_001_ru",
            title = "Tankhwah Na Milne ya Naukri se Nikalne par Haqooq",
            category = LegalCategory.EMPLOYMENT,
            lang = AppLanguage.ROMAN_UR,
            body = "Agar malik aapki kamai hui tankhwah roke ya ghalat tareeqe se naukri se nikale to appointment letter, bank statement aur kaam ke messages jama karein. Aap subai Labour Department ya Labour Court mein claim kar sakte hain.",
            steps = listOf(
                "Appointment letter, bank slips aur WhatsApp/email kaam ke saboot jama karein",
                "Tahreeri notice bhej kar baqaya tankhwah ki mutalba karein",
                "Labour Department ya Payment of Wages Authority se raabta karein",
                "Full-and-final paper sign karne se pehle wakeel se mashwara karein"
            ),
            sourceName = "Labour Laws (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("tankhwah", "salary", "naukri", "job", "malik", "employer"),
            isDraft = true
        ),

        // 9. Tenant / Landlord & Property
        LegalCard(
            id = "property_001",
            title = "Tenant & Landlord Disputes or Illegal Eviction",
            category = LegalCategory.PROPERTY,
            lang = AppLanguage.EN,
            body = "A landlord generally cannot forcibly throw out a tenant or cut utilities without following due legal process before the Rent Controller / Rent Tribunal. Always register your tenancy agreement and keep rent receipts.",
            steps = listOf(
                "Keep a signed tenancy agreement and every rent transfer receipt",
                "Never surrender keys under verbal threats without written settlement",
                "If threatened with forced eviction or utility cutoff, call 15 and seek Rent Tribunal relief",
                "Consult a property lawyer for possession or stay applications"
            ),
            sourceName = "Provincial Rent Restriction Laws (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.MEDIUM,
            keywords = listOf("tenant", "landlord", "kiraya", "rent", "qabza", "property", "zameen"),
            isDraft = true
        ),

        // 10. Consumer Complaint & Online Fraud
        LegalCard(
            id = "consumer_001",
            title = "Defective Products & Consumer Complaints",
            category = LegalCategory.CONSUMER,
            lang = AppLanguage.EN,
            body = "Consumers have rights against defective goods, false advertising, or unfair service charges. Save your invoice, warranty card, and payment proof to file a claim in a Provincial Consumer Protection Court or Authority.",
            steps = listOf(
                "Save original receipt, packaging, screenshots, and seller details",
                "Send a written complaint to the business asking for repair, replacement, or refund",
                "File a complaint with your Provincial Consumer Protection Council / Court",
                "For banking or mobile wallet scams, notify your bank helpline immediately"
            ),
            sourceName = "Provincial Consumer Protection Acts (Pakistan Code)",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.LOW,
            keywords = listOf("consumer", "refund", "defective", "scam", "fraud", "receipt"),
            isDraft = true
        ),

        // 11. What to Save as Evidence & Finding Legal Aid
        LegalCard(
            id = "evidence_001",
            title = "What to Save as Evidence & Finding Legal Aid",
            category = LegalCategory.EMERGENCY,
            lang = AppLanguage.EN,
            body = "Good documentation protects your rights without escalating conflict. Never alter or fake evidence. Save original files, medical reports, and witness contacts securely. Low-income citizens may seek free representation through District Legal Empowerment Committees or Bar Legal Aid cells.",
            steps = listOf(
                "Write a chronological timeline right away while details are fresh",
                "Keep original receipts, screenshots (with visible date/URL), and medico-legal reports",
                "Back up important documents safely or share with a trusted family member",
                "Ask your local District Bar Association or call 1099 about free legal aid"
            ),
            sourceName = "Legal Aid & Justice Authority / Pakistan Code",
            sourceUrl = "https://pakistancode.gov.pk",
            lastReviewed = "2026-11-27",
            reviewer = "HAQOOQ Legal Desk (Draft)",
            urgencyHint = UrgencyLevel.LOW,
            keywords = listOf("evidence", "saboot", "legal aid", "free lawyer", "1099", "timeline"),
            isDraft = true
        )
    )

    fun cardsForLanguage(lang: AppLanguage): List<LegalCard> {
        val exact = allCards.filter { it.lang == lang }
        // Supplement topics that only have English cards so library stays comprehensive in every language
        val coveredIds = exact.map { it.id.substringBefore("_ur").substringBefore("_ru") }.toSet()
        val fallback = allCards.filter {
            it.lang == AppLanguage.EN && it.id !in coveredIds
        }
        return exact + fallback
    }
}
