package com.yehia.prayertimes.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

object QuranTranslationManager {

    private val editionMapping = mapOf(
        "tr" to "tr.ates",
        "fr" to "fr.hamidullah",
        "es" to "es.cortes",
        "de" to "de.aburida",
        "id" to "id.indonesian",
        "ms" to "ms.basmeih",
        "ur" to "ur.junagarhi",
        "fa" to "fa.ansarian",
        "bn" to "bn.bengali",
        "ru" to "ru.kuliev",
        "hi" to "hi.farooq",
        "zh" to "zh.jian",
        "it" to "it.piccardo",
        "pt" to "pt.elhayek",
        "ja" to "ja.japanese",
        "ko" to "ko.korean",
        "sw" to "sw.barwani",
        "ha" to "ha.gumi"
    )

    suspend fun getTranslation(context: Context, surahNumber: Int, lang: String): List<String>? = withContext(Dispatchers.IO) {
        if (lang == "en" || lang == "ar") return@withContext null
        val edition = editionMapping[lang] ?: return@withContext null

        val dir = File(context.filesDir, "quran_translations")
        if (!dir.exists()) dir.mkdirs()

        val cacheFile = File(dir, "surah_${surahNumber}_$lang.json")
        if (cacheFile.exists()) {
            try {
                val cachedJson = cacheFile.readText()
                return@withContext parseTranslationJson(cachedJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fetch from API
        val urlString = "https://api.alquran.cloud/v1/surah/$surahNumber/$edition"
        try {
            val response = URL(urlString).readText()
            val parsed = parseTranslationJson(response)
            if (parsed != null && parsed.isNotEmpty()) {
                cacheFile.writeText(response)
                return@withContext parsed
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }

    private fun parseTranslationJson(jsonString: String): List<String>? {
        return try {
            val json = JSONObject(jsonString)
            val data = json.getJSONObject("data")
            val ayahs = data.getJSONArray("ayahs")
            val list = mutableListOf<String>()
            for (i in 0 until ayahs.length()) {
                val ayah = ayahs.getJSONObject(i)
                list.add(ayah.getString("text"))
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
