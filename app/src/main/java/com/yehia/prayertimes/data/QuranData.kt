package com.yehia.prayertimes.data

data class Ayah(
    val number: Int,
    val textArabic: String,
    val textEnglish: String
)

data class QuranPageStart(
    val page: Int,
    val surahNumber: Int,
    val ayahNumber: Int
)

data class QuranPageAyah(
    val surah: Surah,
    val ayah: Ayah,
    val globalAyahIndex: Int
)

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val revelationType: String,
    val ayahCount: Int,
    val ayahs: List<Ayah>
)

data class QuranDataWrapper(
    val surahs: List<Surah>
)
