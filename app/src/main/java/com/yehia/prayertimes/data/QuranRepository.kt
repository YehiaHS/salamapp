package com.yehia.prayertimes.data

import android.content.Context
import org.json.JSONObject

class QuranRepository(private val context: Context) {

    private var cachedSurahs: List<Surah>? = null
    private var cachedPageStarts: List<QuranPageStart>? = null

    private fun loadFromAssets(): List<Surah> {
        cachedSurahs?.let { return it }

        val jsonString = context.assets.open("quran_data.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val surahsArray = jsonObject.getJSONArray("surahs")
        val surahList = mutableListOf<Surah>()

        for (i in 0 until surahsArray.length()) {
            val surahObj = surahsArray.getJSONObject(i)
            val ayahsArray = surahObj.getJSONArray("ayahs")
            val ayahList = mutableListOf<Ayah>()

            for (j in 0 until ayahsArray.length()) {
                val ayahObj = ayahsArray.getJSONObject(j)
                ayahList.add(
                    Ayah(
                        number = ayahObj.getInt("number"),
                        textArabic = ayahObj.getString("text_arabic"),
                        textEnglish = ayahObj.getString("text_english")
                    )
                )
            }

            surahList.add(
                Surah(
                    number = surahObj.getInt("number"),
                    nameArabic = surahObj.getString("name_arabic"),
                    nameEnglish = surahObj.getString("name_english"),
                    nameTranslation = surahObj.getString("name_translation"),
                    revelationType = surahObj.getString("revelation_type"),
                    ayahCount = surahObj.getInt("ayah_count"),
                    ayahs = ayahList
                )
            )
        }

        cachedSurahs = surahList
        return surahList
    }

    fun getAllSurahs(): List<Surah> {
        return loadFromAssets()
    }

    fun getPageStarts(): List<QuranPageStart> {
        cachedPageStarts?.let { return it }

        val starts = context.assets.open("quran_page_starts.tsv")
            .bufferedReader()
            .useLines { lines ->
                lines.filter { it.isNotBlank() }
                    .map { line ->
                        val parts = line.split('\t')
                        QuranPageStart(
                            page = parts[0].toInt(),
                            surahNumber = parts[1].toInt(),
                            ayahNumber = parts[2].toInt()
                        )
                    }
                    .toList()
            }

        cachedPageStarts = starts
        return starts
    }

    fun getSurah(number: Int): Surah? {
        return loadFromAssets().find { it.number == number }
    }

    fun searchSurahs(query: String): List<Surah> {
        if (query.isBlank()) return loadFromAssets()
        val lowerQuery = query.lowercase()
        return loadFromAssets().filter { surah ->
            surah.nameEnglish.lowercase().contains(lowerQuery) ||
                surah.nameTranslation.lowercase().contains(lowerQuery) ||
                surah.number.toString() == query.trim()
        }
    }
}
