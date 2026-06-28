package com.yehia.prayertimes.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

object LanguageManager {
    private const val PREFS_NAME = "salam_prefs"
    private const val KEY_LANG = "app_language"

    val languages = listOf(
        LangOption("en", "English"),
        LangOption("ar", "العربية"),
        LangOption("tr", "Türkçe"),
        LangOption("fr", "Français"),
        LangOption("es", "Español"),
        LangOption("de", "Deutsch"),
        LangOption("id", "Bahasa Indonesia"),
        LangOption("ms", "Bahasa Melayu"),
        LangOption("ur", "اردو"),
        LangOption("fa", "فارسی"),
        LangOption("bn", "বাংলা"),
        LangOption("ru", "Русский"),
        LangOption("hi", "हिन्दी"),
        LangOption("zh", "中文"),
        LangOption("it", "Italiano"),
        LangOption("pt", "Português"),
        LangOption("ja", "日本語"),
        LangOption("ko", "한국어"),
        LangOption("sw", "Kiswahili"),
        LangOption("ha", "Harshen Hausa")
    )

    private val _currentLang = mutableStateOf("en")
    val currentLang: State<String> = _currentLang

    fun loadLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lang = prefs.getString(KEY_LANG, "en") ?: "en"
        _currentLang.value = lang
        return lang
    }

    fun saveLanguage(context: Context, lang: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, lang).apply()
        _currentLang.value = lang
    }

    private val translations = mapOf(
        "en" to mapOf(
            "tab_prayers" to "Prayers", "tab_quran" to "Quran", "tab_dhikr" to "Dhikr", "tab_duas" to "Duas", "tab_more" to "More",
            "title_preferences" to "Preferences", "title_qibla" to "Qibla Finder", "title_tasbih" to "Tasbih Counter",
            "title_duas" to "Supplications & Adhkar", "title_hadith" to "Hadith of the Day", "title_calendar" to "Hijri Calendar",
            "title_zakat" to "Zakat Calculator", "title_hajj" to "Hajj & Umrah", "title_aideen" to "AiDeen Assistant",
            "title_locator" to "Mosque & Halal Finder", "title_tracker" to "Worship & Fasting", "title_names" to "99 Names of Allah",
            "settings_app_theme" to "App Theme", "settings_amoled" to "AMOLED Pure Black", "settings_app_lang" to "App Language",
            "settings_calc_method" to "Calculation Method", "settings_juristic_method" to "Juristic Method",
            "settings_muezzin_voice" to "Muezzin Voice (Athan)", "settings_about" to "About",
            "settings_gradients" to "Background Gradients", "settings_gradients_desc" to "Show radial glowing accents behind content"
        ),
        "ar" to mapOf(
            "tab_prayers" to "الصلوات", "tab_quran" to "القرآن", "tab_dhikr" to "الأذكار", "tab_duas" to "الأدعية", "tab_more" to "المزيد",
            "title_preferences" to "الإعدادات", "title_qibla" to "بوصلة القبلة", "title_tasbih" to "عداد التسبيح",
            "title_duas" to "الأدعية والأذكار", "title_hadith" to "حديث اليوم", "title_calendar" to "التقويم الهجري",
            "title_zakat" to "حاسبة الزكاة", "title_hajj" to "الحج والعمرة", "title_aideen" to "مساعد أي دين",
            "title_locator" to "المساجد والحلال", "title_tracker" to "متابع العبادات والصيام", "title_names" to "أسماء الله الحسنى",
            "settings_app_theme" to "مظهر التطبيق", "settings_amoled" to "شاشة أموليد سوداء", "settings_app_lang" to "لغة التطبيق",
            "settings_calc_method" to "طريقة الحساب", "settings_juristic_method" to "المذهب الفقهي",
            "settings_muezzin_voice" to "صوت المؤذن (الأذان)", "settings_about" to "حول التطبيق",
            "settings_gradients" to "تدرج الخلفية الملون", "settings_gradients_desc" to "إظهار تأثير إضاءة ملونة خلف المحتوى"
        ),
        "tr" to mapOf(
            "tab_prayers" to "Dualar", "tab_quran" to "Kuran", "tab_dhikr" to "Zikir", "tab_duas" to "Dualar", "tab_more" to "Daha Fazla",
            "title_preferences" to "Tercihler", "title_qibla" to "Kıble Bulucu", "title_tasbih" to "Tesbih Sayacı",
            "title_duas" to "Dualar ve Zikirler", "title_hadith" to "Günün Hadisi", "title_calendar" to "Hicri Takvim",
            "title_zakat" to "Zekat Hesaplayıcı", "title_hajj" to "Hac ve Umre", "title_aideen" to "AiDeen Asistanı",
            "title_locator" to "Cami ve Helal Bulucu", "title_tracker" to "İbadet ve Oruç Takibi", "title_names" to "Esma-ül Hüsna",
            "settings_app_theme" to "Uygulama Teması", "settings_amoled" to "AMOLED Saf Siyah", "settings_app_lang" to "Uygulama Dili",
            "settings_calc_method" to "Hesaplama Yöntemi", "settings_juristic_method" to "Mezhep Seçimi",
            "settings_muezzin_voice" to "Müezzin Sesi (Ezan)", "settings_about" to "Hakkında"
        ),
        "fr" to mapOf(
            "tab_prayers" to "Prières", "tab_quran" to "Coran", "tab_dhikr" to "Dhikr", "tab_duas" to "Douas", "tab_more" to "Plus",
            "title_preferences" to "Préférences", "title_qibla" to "Boussole Qibla", "title_tasbih" to "Compteur Tasbih",
            "title_duas" to "Supplications & Adhkar", "title_hadith" to "Hadith du Jour", "title_calendar" to "Calendrier Hijri",
            "title_zakat" to "Calculateur Zakat", "title_hajj" to "Hajj & Omra", "title_aideen" to "Assistant AiDeen",
            "title_locator" to "Mosquée & Restos Halal", "title_tracker" to "Suivi Culte & Jeûne", "title_names" to "99 Noms d'Allah",
            "settings_app_theme" to "Thème de l'App", "settings_amoled" to "AMOLED Noir Pur", "settings_app_lang" to "Langue de l'App",
            "settings_calc_method" to "Méthode de Calcul", "settings_juristic_method" to "Méthode Juridique",
            "settings_muezzin_voice" to "Voix du Muezzin (Adhan)", "settings_about" to "À Propos"
        ),
        "es" to mapOf(
            "tab_prayers" to "Oraciones", "tab_quran" to "Corán", "tab_dhikr" to "Dhikr", "tab_duas" to "Duas", "tab_more" to "Más",
            "title_preferences" to "Preferencias", "title_qibla" to "Buscador de Qibla", "title_tasbih" to "Contador de Tasbih",
            "title_duas" to "Súplicas y Adhkar", "title_hadith" to "Hadith del Día", "title_calendar" to "Calendario Hiyri",
            "title_zakat" to "Calculadora de Zakat", "title_hajj" to "Hajj y Umrah", "title_aideen" to "Asistente AiDeen",
            "title_locator" to "Mezquitas y Halal", "title_tracker" to "Control de Culto", "title_names" to "99 Nombres de Alá",
            "settings_app_theme" to "Tema de la App", "settings_amoled" to "AMOLED Negro Puro", "settings_app_lang" to "Idioma de la App",
            "settings_calc_method" to "Método de Cálculo", "settings_juristic_method" to "Escuela Jurídica",
            "settings_muezzin_voice" to "Voz del Muecín (Adhan)", "settings_about" to "Acerca de"
        ),
        "de" to mapOf(
            "tab_prayers" to "Gebete", "tab_quran" to "Koran", "tab_dhikr" to "Dhikr", "tab_duas" to "Duas", "tab_more" to "Mehr",
            "title_preferences" to "Einstellungen", "title_qibla" to "Qibla-Finder", "title_tasbih" to "Tasbih-Zähler",
            "title_duas" to "Bittgebete & Adhkar", "title_hadith" to "Hadith des Tages", "title_calendar" to "Hicri-Kalender",
            "title_zakat" to "Zakat-Rechner", "title_hajj" to "Haddsch & Umra", "title_aideen" to "AiDeen Assistent",
            "title_locator" to "Moschee & Halal Finder", "title_tracker" to "Kult & Fasten Tracker", "title_names" to "99 Namen Allahs",
            "settings_app_theme" to "App-Design", "settings_amoled" to "AMOLED Reines Schwarz", "settings_app_lang" to "App-Sprache",
            "settings_calc_method" to "Berechnungsmethode", "settings_juristic_method" to "Rechtsschule",
            "settings_muezzin_voice" to "Muezzin-Stimme (Adhan)", "settings_about" to "Über"
        ),
        "id" to mapOf(
            "tab_prayers" to "Salat", "tab_quran" to "Al-Quran", "tab_dhikr" to "Dzikir", "tab_duas" to "Doa", "tab_more" to "Lainnya",
            "title_preferences" to "Pengaturan", "title_qibla" to "Kompas Kiblat", "title_tasbih" to "Penghitung Tasbih",
            "title_duas" to "Kumpulan Doa & Dzikir", "title_hadith" to "Hadits Hari Ini", "title_calendar" to "Kalender Hijriah",
            "title_zakat" to "Kalkulator Zakat", "title_hajj" to "Haji & Umrah", "title_aideen" to "Asisten AiDeen",
            "title_locator" to "Cari Masjid & Halal", "title_tracker" to "Pelacak Ibadah & Puasa", "title_names" to "99 Asmaul Husna",
            "settings_app_theme" to "Tema Aplikasi", "settings_amoled" to "AMOLED Hitam Pekat", "settings_app_lang" to "Bahasa Aplikasi",
            "settings_calc_method" to "Metode Perhitungan", "settings_juristic_method" to "Madzhab Fiqih",
            "settings_muezzin_voice" to "Suara Muezzin (Adzan)", "settings_about" to "Tentang"
        ),
        "ms" to mapOf(
            "tab_prayers" to "Solat", "tab_quran" to "Al-Quran", "tab_dhikr" to "Zikir", "tab_duas" to "Doa", "tab_more" to "Lain-lain",
            "title_preferences" to "Pilihan", "title_qibla" to "Arah Kiblat", "title_tasbih" to "Pengira Tesbih",
            "title_duas" to "Doa & Adhkar", "title_hadith" to "Hadis Pilihan", "title_calendar" to "Kalendar Hijriah",
            "title_zakat" to "Kalkulator Zakat", "title_hajj" to "Haji & Umrah", "title_aideen" to "Asisten AiDeen",
            "title_locator" to "Cari Masjid & Halal", "title_tracker" to "Rekod Ibadah & Puasa", "title_names" to "99 Nama Allah",
            "settings_app_theme" to "Tema Aplikasi", "settings_amoled" to "AMOLED Hitam Pekat", "settings_app_lang" to "Bahasa Aplikasi",
            "settings_calc_method" to "Kaedah Pengiraan", "settings_juristic_method" to "Mazhab Fekah",
            "settings_muezzin_voice" to "Suara Muezzin (Azan)", "settings_about" to "Mengenai"
        ),
        "ur" to mapOf(
            "tab_prayers" to "نمازیں", "tab_quran" to "قرآن", "tab_dhikr" to "ذکر", "tab_duas" to "دعائیں", "tab_more" to "مزید",
            "title_preferences" to "ترجیحات", "title_qibla" to "قبلہ نما", "title_tasbih" to "تسبیح کاؤنٹر",
            "title_duas" to "دعائیں اور اذکار", "title_hadith" to "آج کی حدیث", "title_calendar" to "ہجری تقویم",
            "title_zakat" to "زکوٰۃ کیلکولیٹر", "title_hajj" to "حج و عمرہ", "title_aideen" to "آئی دین اسسٹنٹ",
            "title_locator" to "مسجد اور حلال گائیڈ", "title_tracker" to "عبادت اور روزہ ٹریکر", "title_names" to "اللہ کے 99 نام",
            "settings_app_theme" to "ایپ تھیم", "settings_amoled" to "AMOLED خالص سیاہ", "settings_app_lang" to "ایپ کی زبان",
            "settings_calc_method" to "حساب کا طریقہ", "settings_juristic_method" to "فقہی مسلک",
            "settings_muezzin_voice" to "مؤذن کی آواز (اذان)", "settings_about" to "تعارف"
        ),
        "fa" to mapOf(
            "tab_prayers" to "نمازها", "tab_quran" to "قرآن", "tab_dhikr" to "ذکر", "tab_duas" to "دعاها", "tab_more" to "بیشتر",
            "title_preferences" to "تنظیمات", "title_qibla" to "قبله نما", "title_tasbih" to "تسبیح شمار",
            "title_duas" to "ادعیه و اذکار", "title_hadith" to "حدیث روز", "title_calendar" to "تقویم هجری",
            "title_zakat" to "محاسبه زکات", "title_hajj" to "حج و عمره", "title_aideen" to "دستیار آی دین",
            "title_locator" to "مسجد یاب و حلال", "title_tracker" to "ردیاب عبادت و روزه", "title_names" to "۹۹ نام خدا",
            "settings_app_theme" to "پوسته برنامه", "settings_amoled" to "AMOLED مشکی خالص", "settings_app_lang" to "زبان برنامه",
            "settings_calc_method" to "روش محاسبه", "settings_juristic_method" to "مذهب فقهی",
            "settings_muezzin_voice" to "صدای مؤذن (اذان)", "settings_about" to "درباره"
        ),
        "bn" to mapOf(
            "tab_prayers" to "নামাজ", "tab_quran" to "কোরআন", "tab_dhikr" to "জিকির", "tab_duas" to "দোয়া", "tab_more" to "আরো",
            "title_preferences" to "পছন্দসমূহ", "title_qibla" to "কিবলা কম্পাস", "title_tasbih" to "তাসবিহ কাউন্টার",
            "title_duas" to "দোয়া ও আজকার", "title_hadith" to "আজকের হাদিস", "title_calendar" to "হিজরি ক্যালেন্ডার",
            "title_zakat" to "যাকাত ক্যালকুলেটর", "title_hajj" to "হজ্জ ও উমরাহ", "title_aideen" to "আই দীন অ্যাসিস্ট্যান্ট",
            "title_locator" to "মসজিদ ও হালাল খাবার", "title_tracker" to "ইবাদত ও রোজা ট্র্যাকার", "title_names" to "আল্লাহর ৯৯ নাম",
            "settings_app_theme" to "অ্যাপ থিম", "settings_amoled" to "অ্যামোলেড পিওর ব্ল্যাক", "settings_app_lang" to "অ্যাপের ভাষা",
            "settings_calc_method" to "গণনা পদ্ধতি", "settings_juristic_method" to "মাজহাব ফিকহ",
            "settings_muezzin_voice" to "মুয়াজ্জিনের কণ্ঠ (আযান)", "settings_about" to "সম্পর্কে"
        ),
        "ru" to mapOf(
            "tab_prayers" to "Молитвы", "tab_quran" to "Коран", "tab_dhikr" to "Зикр", "tab_duas" to "Дуа", "tab_more" to "Еще",
            "title_preferences" to "Настройки", "title_qibla" to "Компас Киблы", "title_tasbih" to "Счетчик тасбиха",
            "title_duas" to "Молитвы и Азкар", "title_hadith" to "Хадис дня", "title_calendar" to "Мусульманский календарь",
            "title_zakat" to "Калькулятор закята", "title_hajj" to "Хадж и Умра", "title_aideen" to "Ассистент AiDeen",
            "title_locator" to "Поиск мечетей и халяля", "title_tracker" to "Контроль молитв и поста", "title_names" to "99 имен Аллаха",
            "settings_app_theme" to "Тема приложения", "settings_amoled" to "AMOLED Чисто черный", "settings_app_lang" to "Язык приложения",
            "settings_calc_method" to "Метод расчета", "settings_juristic_method" to "Мазхаб",
            "settings_muezzin_voice" to "Голос муэдзина (Азан)", "settings_about" to "О программе"
        ),
        "hi" to mapOf(
            "tab_prayers" to "नमाज़", "tab_quran" to "क़ुरान", "tab_dhikr" to "ज़िक्र", "tab_duas" to "दुआएं", "tab_more" to "अधिक",
            "title_preferences" to "प्राथमिकताएं", "title_qibla" to "क़िबला दिशा", "title_tasbih" to "तस्बीह काउंटर",
            "title_duas" to "दुआ और अज़कार", "title_hadith" to "आज की हदीस", "title_calendar" to "हिजरी कैलेंडर",
            "title_zakat" to "ज़कात कैलकुलेटर", "title_hajj" to "हज और उमराह", "title_aideen" to "ऐदीन सहायक",
            "title_locator" to "मस्जिद और हलाल खोजें", "title_tracker" to "इबादत और रोज़ा ट्रैकर", "title_names" to "अल्लाह के 99 नाम",
            "settings_app_theme" to "ऐप थीम", "settings_amoled" to "अमोलेड शुद्ध काला", "settings_app_lang" to "ऐप की भाषा",
            "settings_calc_method" to "गणना विधि", "settings_juristic_method" to "मज़हब स्कूल",
            "settings_muezzin_voice" to "मुअज़्ज़ิน की आवाज़ (अज़ान)", "settings_about" to "विवरण"
        ),
        "zh" to mapOf(
            "tab_prayers" to "礼拜", "tab_quran" to "古兰经", "tab_dhikr" to "赞念", "tab_duas" to "祈祷", "tab_more" to "更多",
            "title_preferences" to "应用设置", "title_qibla" to "克尔白朝向", "title_tasbih" to "电子念珠",
            "title_duas" to "祈祷词与赞词", "title_hadith" to "今日圣训", "title_calendar" to "伊斯兰历法",
            "title_zakat" to "天课计算器", "title_hajj" to "朝觐与副朝", "title_aideen" to "AI 信仰助手",
            "title_locator" to "清真寺与清真餐馆", "title_tracker" to "功课与斋戒打卡", "title_names" to "真主的九十九尊名",
            "settings_app_theme" to "应用主题", "settings_amoled" to "AMOLED纯黑模式", "settings_app_lang" to "应用语言",
            "settings_calc_method" to "计算标准", "settings_juristic_method" to "教法学派",
            "settings_muezzin_voice" to "宣礼声音 (阿赞)", "settings_about" to "关于软件"
        ),
        "it" to mapOf(
            "tab_prayers" to "Preghiere", "tab_quran" to "Corano", "tab_dhikr" to "Dhikr", "tab_duas" to "Dua", "tab_more" to "Altro",
            "title_preferences" to "Preferenze", "title_qibla" to "Bussola Qibla", "title_tasbih" to "Contatore Tasbih",
            "title_duas" to "Suppliche & Adhkar", "title_hadith" to "Hadith del Giorno", "title_calendar" to "Calendario Islamico",
            "title_zakat" to "Calcolo Zakat", "title_hajj" to "Hajj e Umrah", "title_aideen" to "Assistente AiDeen",
            "title_locator" to "Moschee e Halal Finder", "title_tracker" to "Registro Preghiere e Digiuno", "title_names" to "99 Nomi di Allah",
            "settings_app_theme" to "Tema dell'App", "settings_amoled" to "AMOLED Nero Assoluto", "settings_app_lang" to "Lingua dell'App",
            "settings_calc_method" to "Metodo di Calcolo", "settings_juristic_method" to "Scuola Giuridica",
            "settings_muezzin_voice" to "Voce del Muezzin (Adhan)", "settings_about" to "Info"
        ),
        "pt" to mapOf(
            "tab_prayers" to "Orações", "tab_quran" to "Alcorão", "tab_dhikr" to "Dhikr", "tab_duas" to "Duas", "tab_more" to "Mais",
            "title_preferences" to "Preferências", "title_qibla" to "Bússola de Qibla", "title_tasbih" to "Contador de Tasbih",
            "title_duas" to "Súplicas e Adhkar", "title_hadith" to "Hadith do Dia", "title_calendar" to "Calendário Islâmico",
            "title_zakat" to "Calculadora de Zakat", "title_hajj" to "Hajj e Umrah", "title_aideen" to "Assistente AiDeen",
            "title_locator" to "Mesquita e Halal Finder", "title_tracker" to "Suivi de Culto e Jejum", "title_names" to "99 Nomes de Deus",
            "settings_app_theme" to "Tema do App", "settings_amoled" to "AMOLED Preto Puro", "settings_app_lang" to "Idioma do App",
            "settings_calc_method" to "Método de Cálculo", "settings_juristic_method" to "Escola Jurídica",
            "settings_muezzin_voice" to "Voz do Muezim (Adhan)", "settings_about" to "Sobre"
        ),
        "ja" to mapOf(
            "tab_prayers" to "礼拝", "tab_quran" to "コーラン", "tab_dhikr" to "ジクル", "tab_duas" to "ドゥア", "tab_more" to "その他",
            "title_preferences" to "設定", "title_qibla" to "キブラ方向", "title_tasbih" to "タスビーフ計数",
            "title_duas" to "祈りと言葉", "title_hadith" to "今日のハディース", "title_calendar" to "ヒジュラ暦",
            "title_zakat" to "ザカート計算", "title_hajj" to "巡礼ガイド", "title_aideen" to "AIアシスタント",
            "title_locator" to "モスク・ハラール検索", "title_tracker" to "礼拝と断食の記録", "title_names" to "アッラーの99の美名",
            "settings_app_theme" to "アプリテーマ", "settings_amoled" to "AMOLED漆黒モード", "settings_app_lang" to "アプリの言語",
            "settings_calc_method" to "計算方法", "settings_juristic_method" to "法学派別",
            "settings_muezzin_voice" to "アザーン音声", "settings_about" to "情報"
        ),
        "ko" to mapOf(
            "tab_prayers" to "예배", "tab_quran" to "꾸란", "tab_dhikr" to "디크르", "tab_duas" to "두아", "tab_more" to "더보기",
            "title_preferences" to "환경 설정", "title_qibla" to "키블라 나침반", "title_tasbih" to "타스비흐 카운터",
            "title_duas" to "기도문 및 아드카르", "title_hadith" to "오늘의 하디스", "title_calendar" to "이슬람력",
            "title_zakat" to "자카트 계산기", "title_hajj" to "하지 & 움라", "title_aideen" to "AI 신앙 비서",
            "title_locator" to "모스크 & 할랄 검색", "title_tracker" to "예배 및 단식 기록기", "title_names" to "알라의 99개 이름",
            "settings_app_theme" to "앱 테마", "settings_amoled" to "AMOLED 퓨어 블랙", "settings_app_lang" to "앱 언어",
            "settings_calc_method" to "계산 기준", "settings_juristic_method" to "법학파 선택",
            "settings_muezzin_voice" to "무에진 목소리 (아잔)", "settings_about" to "정보"
        ),
        "sw" to mapOf(
            "tab_prayers" to "Sala", "tab_quran" to "Kurani", "tab_dhikr" to "Dhikri", "tab_duas" to "Duwa", "tab_more" to "Zaidi",
            "title_preferences" to "Mipangilio", "title_qibla" to "Dira ya Qibla", "title_tasbih" to "Kikokotoo cha Tasbih",
            "title_duas" to "Duwa na Adhkar", "title_hadith" to "Hadithi ya Leo", "title_calendar" to "Kalenda ya Hijri",
            "title_zakat" to "Kikokotoo cha Zakat", "title_hajj" to "Hijja na Umrah", "title_aideen" to "Msaidizi wa AiDeen",
            "title_locator" to "Tafuta Msikiti na Halal", "title_tracker" to "Kumbukumbu ya Ibada & Saumu", "title_names" to "Majina 99 ya Allah",
            "settings_app_theme" to "Mada ya Programu", "settings_amoled" to "AMOLED Nyeusi Safi", "settings_app_lang" to "Lugha ya Programu",
            "settings_calc_method" to "Njia ya Mahesabu", "settings_juristic_method" to "Madhehebu ya Fiqh",
            "settings_muezzin_voice" to "Sauti ya Muezzin (Adhana)", "settings_about" to "Kuhusu"
        ),
        "ha" to mapOf(
            "tab_prayers" to "Salloli", "tab_quran" to "Alkur'ani", "tab_dhikr" to "Zikiri", "tab_duas" to "Addu'o'i", "tab_more" to "Karin Bayani",
            "title_preferences" to "Zaɓuɓɓuka", "title_qibla" to "Neman Alkibila", "title_tasbih" to "Lissafin Tasbihi",
            "title_duas" to "Addu'o'i da Zikirai", "title_hadith" to "Hadisin Yau", "title_calendar" to "Tarihin Musulunci",
            "title_zakat" to "Lissafin Zakka", "title_hajj" to "Aikin Hajji da Umra", "title_aideen" to "Mataimakin AiDeen",
            "title_locator" to "Neman Masallaci da Abincin Halal", "title_tracker" to "Bibiyar Salloli da Azumi", "title_names" to "Sunayen Allah 99",
            "settings_app_theme" to "Jigon Manhajja", "settings_amoled" to "AMOLED Baki Sosai", "settings_app_lang" to "Harshen Manhajja",
            "settings_calc_method" to "Hanyar Lissafi", "settings_juristic_method" to "Mazhabin Fikh",
            "settings_muezzin_voice" to "Muryar Ladani (Kiran Salla)", "settings_about" to "Game da Manhajja"
        )
    )

    private val extraTranslations = mapOf(
        "en" to mapOf(
            "fajr" to "Fajr", "sunrise" to "Sunrise", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Isha", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Notifications Preferences", "mode_athan" to "Athan", "mode_beep" to "Beep", "mode_silent" to "Silent", "mode_off" to "Off",
            "next_prayer" to "Next Prayer", "now" to "Now", "todays_prayers" to "Today's Prayers",
            "pre_prayer_reminder" to "Early Reminder", "iqamah_reminder" to "Iqamah Alert", "minutes_before" to "mins before", "minutes_after" to "mins after", "none" to "None",
            "sunnah_fajr" to "Sunnah: 2 Rak'ahs before", "sunnah_dhuhr" to "Sunnah: 4 Rak'ahs before, 2 after", "sunnah_asr" to "Sunnah: 4 Rak'ahs before (Recommended)", "sunnah_maghrib" to "Sunnah: 2 Rak'ahs after", "sunnah_isha" to "Sunnah: 2 Rak'ahs after",
            "sub_fajr" to "Dawn", "sub_sunrise" to "Sunrise", "sub_dhuhr" to "Midday", "sub_asr" to "Afternoon", "sub_maghrib" to "Sunset", "sub_isha" to "Night", "sub_qiyam" to "Last third of the night",
            "greeting" to "Assalamu Alaikum", "salam" to "Salam", "may_peace" to "May peace be upon you", "hijri_date" to "Hijri Date"
        ),
        "ar" to mapOf(
            "fajr" to "الفجر", "sunrise" to "الشروق", "dhuhr" to "الظهر", "asr" to "العصر", "maghrib" to "المغرب", "isha" to "العشاء", "qiyam" to "قيام الليل",
            "settings_notifications" to "إعدادات الإشعارات", "mode_athan" to "أذان", "mode_beep" to "رنين", "mode_silent" to "صامت", "mode_off" to "إيقاف",
            "next_prayer" to "الصلاة القادمة", "now" to "الآن", "todays_prayers" to "صلوات اليوم",
            "pre_prayer_reminder" to "تذكير مبكر", "iqamah_reminder" to "تنبيه الإقامة", "minutes_before" to "دقيقة قبل", "minutes_after" to "دقيقة بعد", "none" to "لا يوجد",
            "sunnah_fajr" to "السنة: ٢ ركعة قبل الفريضة", "sunnah_dhuhr" to "السنة: ٤ ركعات قبل و٢ بعد", "sunnah_asr" to "السنة: ٤ ركعات قبل (مستحبة)", "sunnah_maghrib" to "السنة: ٢ ركعة بعد", "sunnah_isha" to "السنة: ٢ ركعة بعد",
            "sub_fajr" to "الفجر", "sub_sunrise" to "شروق الشمس", "sub_dhuhr" to "الزوال", "sub_asr" to "المساء", "sub_maghrib" to "غروب الشمس", "sub_isha" to "الليل", "sub_qiyam" to "ثلث الليل الآخر",
            "greeting" to "السلام عليكم", "salam" to "سلام", "may_peace" to "السلام عليكم ورحمة الله وبركاته", "hijri_date" to "التاريخ الهجري"
        ),
        "tr" to mapOf(
            "fajr" to "Fecr", "sunrise" to "Güneş", "dhuhr" to "Öğle", "asr" to "İkindi", "maghrib" to "Akşam", "isha" to "Yatsı", "qiyam" to "Teheccüd",
            "settings_notifications" to "Bildirim Tercihleri", "mode_athan" to "Ezan", "mode_beep" to "Bip", "mode_silent" to "Sessiz", "mode_off" to "Kapalı",
            "next_prayer" to "Sıraaki Ezan", "now" to "Şimdi", "todays_prayers" to "Bugünün Ezan Vakitleri"
        ),
        "fr" to mapOf(
            "fajr" to "Fajr", "sunrise" to "Lever du soleil", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Isha", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Préférences de Notifications", "mode_athan" to "Adhan", "mode_beep" to "Bip", "mode_silent" to "Silencieux", "mode_off" to "Désactivé",
            "next_prayer" to "Prochaine Prière", "now" to "Maintenant", "todays_prayers" to "Prières d'Aujourd'hui"
        ),
        "es" to mapOf(
            "fajr" to "Fajr", "sunrise" to "Amanecer", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Isha", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Preferencias de Notificaciones", "mode_athan" to "Adhan", "mode_beep" to "Pitido", "mode_silent" to "Silencioso", "mode_off" to "Apagado",
            "next_prayer" to "Próxima Oración", "now" to "Ahora", "todays_prayers" to "Oraciones de Hoy"
        ),
        "de" to mapOf(
            "fajr" to "Fadschr", "sunrise" to "Sonnenaufgang", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Ischa", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Benachrichtigungseinstellungen", "mode_athan" to "Adhan", "mode_beep" to "Signalton", "mode_silent" to "Stumm", "mode_off" to "Aus",
            "next_prayer" to "Nächstes Gebet", "now" to "Jetzt", "todays_prayers" to "Heutige Gebete"
        ),
        "id" to mapOf(
            "fajr" to "Subuh", "sunrise" to "Terbit", "dhuhr" to "Dzuhur", "asr" to "Ashar", "maghrib" to "Maghrib", "isha" to "Isya", "qiyam" to "Qiyamul Lail",
            "settings_notifications" to "Preferensi Notifikasi", "mode_athan" to "Adzan", "mode_beep" to "Bip", "mode_silent" to "Senyap", "mode_off" to "Mati",
            "next_prayer" to "Salat Berikutnya", "now" to "Sekarang", "todays_prayers" to "Jadwal Salat Hari Ini"
        ),
        "ms" to mapOf(
            "fajr" to "Subuh", "sunrise" to "Syuruk", "dhuhr" to "Zohor", "asr" to "Asar", "maghrib" to "Maghrib", "isha" to "Isyak", "qiyam" to "Qiyamullail",
            "settings_notifications" to "Pilihan Pemberitahuan", "mode_athan" to "Azan", "mode_beep" to "Bip", "mode_silent" to "Senyap", "mode_off" to "Tutup",
            "next_prayer" to "Solat Seterusnya", "now" to "Sekarang", "todays_prayers" to "Waktu Solat Hari Ini"
        ),
        "ur" to mapOf(
            "fajr" to "فجر", "sunrise" to "طلوع آفتاب", "dhuhr" to "ظہر", "asr" to "عصر", "maghrib" to "مغرب", "isha" to "عشاء", "qiyam" to "قیام الليل",
            "settings_notifications" to "اطلاعات کی ترجیحات", "mode_athan" to "اذان", "mode_beep" to "بیپ", "mode_silent" to "خاموش", "mode_off" to "بند",
            "next_prayer" to "اگلی نماز", "now" to "اب", "todays_prayers" to "آج کی نمازیں"
        ),
        "fa" to mapOf(
            "fajr" to "فجر", "sunrise" to "طلوع آفتاب", "dhuhr" to "ظهر", "asr" to "عصر", "maghrib" to "مغرب", "isha" to "عشا", "qiyam" to "قیام الیل",
            "settings_notifications" to "تنظیمات اعلان‌ها", "mode_athan" to "اذان", "mode_beep" to "بوق", "mode_silent" to "بی‌صدا", "mode_off" to "خاموش",
            "next_prayer" to "نماز بعدی", "now" to "اکنون", "todays_prayers" to "نمازهای امروز"
        ),
        "bn" to mapOf(
            "fajr" to "ফজর", "sunrise" to "সূর্যোদয়", "dhuhr" to "যোহর", "asr" to "আসর", "maghrib" to "মাগরিব", "isha" to "এশা", "qiyam" to "কিয়ামুল লাইল",
            "settings_notifications" to "বিজ্ঞপ্তি সেটিংস", "mode_athan" to "আযান", "mode_beep" to "বীপ", "mode_silent" to "নীরব", "mode_off" to "বন্ধ",
            "next_prayer" to "পরবর্তী নামায", "now" to "এখন", "todays_prayers" to "আজকের নামাজ"
        ),
        "ru" to mapOf(
            "fajr" to "Фаджр", "sunrise" to "Восход", "dhuhr" to "Зухр", "asr" to "Аср", "maghrib" to "Магриб", "isha" to "Иша", "qiyam" to "Кийам (Тахаджуд)",
            "settings_notifications" to "Настройки уведомлений", "mode_athan" to "Азан", "mode_beep" to "Звуковой сигнал", "mode_silent" to "Без звука", "mode_off" to "Выкл",
            "next_prayer" to "Следующая молитва", "now" to "Сейчас", "todays_prayers" to "Молитвы на сегодня"
        ),
        "hi" to mapOf(
            "fajr" to "फज्र", "sunrise" to "सूर्योदय", "dhuhr" to "जुह्र", "asr" to "अस्र", "maghrib" to "मगरिब", "isha" to "इशा", "qiyam" to "क़ियाम (तहज्जुद)",
            "settings_notifications" to "अधिसूचना प्राथमिकताएं", "mode_athan" to "अज़ान", "mode_beep" to "बीप", "mode_silent" to "मौन", "mode_off" to "बंद",
            "next_prayer" to "अगली प्रार्थना", "now" to "अभी", "todays_prayers" to "आज की नमाज़ें"
        ),
        "zh" to mapOf(
            "fajr" to "晨礼", "sunrise" to "日出", "dhuhr" to "晌礼", "asr" to "晡礼", "maghrib" to "昏礼", "isha" to "宵礼", "qiyam" to "夜间立礼",
            "settings_notifications" to "通知设置", "mode_athan" to "阿赞", "mode_beep" to "提示音", "mode_silent" to "静音", "mode_off" to "关闭",
            "next_prayer" to "下一次礼拜", "now" to "当前", "todays_prayers" to "今日礼拜时间"
        ),
        "it" to mapOf(
            "fajr" to "Fajr", "sunrise" to "Alba", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Isha", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Preferenze Notifiche", "mode_athan" to "Adhan", "mode_beep" to "Bip", "mode_silent" to "Silenzioso", "mode_off" to "Disattivato",
            "next_prayer" to "Prossima Preghiera", "now" to "Ora", "todays_prayers" to "Preghiere di Oggi"
        ),
        "pt" to mapOf(
            "fajr" to "Fajr", "sunrise" to "Nascer do sol", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Isha", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Preferências de Notificação", "mode_athan" to "Adhan", "mode_beep" to "Bip", "mode_silent" to "Silencioso", "mode_off" to "Desativado",
            "next_prayer" to "Próxima Oração", "now" to "Agora", "todays_prayers" to "Orações de Hoje"
        ),
        "ja" to mapOf(
            "fajr" to "ファジュル", "sunrise" to "日の出", "dhuhr" to "ズフル", "asr" to "アスル", "maghrib" to "マグリブ", "isha" to "イシャー", "qiyam" to "キヤム",
            "settings_notifications" to "通知設定", "mode_athan" to "アザーン", "mode_beep" to "ビープ音", "mode_silent" to "サイレント", "mode_off" to "オフ",
            "next_prayer" to "次の礼拝", "now" to "現在", "todays_prayers" to "今日の礼拝時間"
        ),
        "ko" to mapOf(
            "fajr" to "파즈르", "sunrise" to "일출", "dhuhr" to "두흐르", "asr" to "아스르", "maghrib" to "마그립", "isha" to "이샤", "qiyam" to "키얌 (타하주드)",
            "settings_notifications" to "알림 설정", "mode_athan" to "아잔", "mode_beep" to "비프음", "mode_silent" to "무음", "mode_off" to "꺼짐",
            "next_prayer" to "다음 예배", "now" to "현재", "todays_prayers" to "오늘의 예배 시간"
        ),
        "sw" to mapOf(
            "fajr" to "Aljiri", "sunrise" to "Macheo", "dhuhr" to "Adhuhuri", "asr" to "Alasiri", "maghrib" to "Magharibi", "isha" to "Isha", "qiyam" to "Qiyam (Tahajjud)",
            "settings_notifications" to "Mipangilio ya Arifa", "mode_athan" to "Adhana", "mode_beep" to "Bibi", "mode_silent" to "Kimya", "mode_off" to "Zima",
            "next_prayer" to "Sala Inayofuata", "now" to "Sasa", "todays_prayers" to "Sala za Leo"
        ),
        "ha" to mapOf(
            "fajr" to "Asuba", "sunrise" to "Hantsi", "dhuhr" to "Rana", "asr" to "La'asar", "maghrib" to "Almagurub", "isha" to "Lisha", "qiyam" to "Tahajjud",
            "settings_notifications" to "Zaɓin Sanarwa", "mode_athan" to "Kiran Salla", "mode_beep" to "Ƙara", "mode_silent" to "Shiru", "mode_off" to "Kashe",
            "next_prayer" to "Sallah Ta Gaba", "now" to "Yanzu", "todays_prayers" to "Sallolin Yau"
        )
    )

    fun get(key: String): String {
        return extraTranslations[_currentLang.value]?.get(key)
            ?: translations[_currentLang.value]?.get(key)
            ?: extraTranslations["en"]?.get(key)
            ?: translations["en"]?.get(key)
            ?: key
    }

    fun isRtl(): Boolean {
        val lang = _currentLang.value
        return lang == "ar" || lang == "ur" || lang == "fa"
    }

    fun localizeNumerals(text: String): String {
        if (_currentLang.value != "ar") return text
        return text.map { char ->
            when (char) {
                '0' -> '٠'
                '1' -> '١'
                '2' -> '٢'
                '3' -> '٣'
                '4' -> '٤'
                '5' -> '٥'
                '6' -> '٦'
                '7' -> '٧'
                '8' -> '٨'
                '9' -> '٩'
                else -> char
            }
        }.joinToString("")
    }

    fun formatDisplayTime(timeStr: String): String {
        var result = timeStr
        if (_currentLang.value == "ar") {
            result = result
                .replace("AM", "ص")
                .replace("PM", "م")
                .replace("am", "ص")
                .replace("pm", "م")
            return localizeNumerals(result)
        }
        return result
    }
}

data class LangOption(val code: String, val name: String)
