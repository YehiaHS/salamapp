package com.yehia.prayertimes.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yehia.prayertimes.data.QuranRepository
import com.yehia.prayertimes.data.QuranPageStart
import com.yehia.prayertimes.data.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuranRepository(application.applicationContext)

    private val _surahs = MutableStateFlow<List<Surah>>(emptyList())
    val surahs: StateFlow<List<Surah>> = _surahs.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSurah = MutableStateFlow<Surah?>(null)
    val selectedSurah: StateFlow<Surah?> = _selectedSurah.asStateFlow()

    private val _selectedPage = MutableStateFlow<Int?>(null)
    val selectedPage: StateFlow<Int?> = _selectedPage.asStateFlow()

    private val _pageStarts = MutableStateFlow<List<QuranPageStart>>(emptyList())
    val pageStarts: StateFlow<List<QuranPageStart>> = _pageStarts.asStateFlow()

    private val _bookmarkSurah = MutableStateFlow<Int>(0)
    val bookmarkSurah: StateFlow<Int> = _bookmarkSurah.asStateFlow()

    private val _bookmarkAyah = MutableStateFlow<Int>(0)
    val bookmarkAyah: StateFlow<Int> = _bookmarkAyah.asStateFlow()

    private val _translatedAyahs = MutableStateFlow<Map<Int, String>>(emptyMap())
    val translatedAyahs: StateFlow<Map<Int, String>> = _translatedAyahs.asStateFlow()

    // Interactive custom preferences inside the reader
    private val _readerFontSize = MutableStateFlow(22f)
    val readerFontSize: StateFlow<Float> = _readerFontSize.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllSurahs()
            _surahs.value = list
            _pageStarts.value = repository.getPageStarts()
            _isLoading.value = false
        }
        loadBookmark()
        loadReaderFontSize()
    }

    val filteredSurahs: StateFlow<List<Surah>> = combine(_surahs, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val lowerQuery = query.lowercase()
            list.filter { surah ->
                surah.nameEnglish.lowercase().contains(lowerQuery) ||
                surah.nameTranslation.lowercase().contains(lowerQuery) ||
                surah.number.toString() == query.trim()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun selectSurah(surah: Surah) {
        _selectedSurah.value = surah
        _selectedPage.value = pageStarts.value.lastOrNull {
            it.surahNumber < surah.number || (it.surahNumber == surah.number && it.ayahNumber <= 1)
        }?.page ?: pageStarts.value.firstOrNull { it.surahNumber == surah.number }?.page
    }

    fun selectSurahById(number: Int) {
        val surah = _surahs.value.find { it.number == number }
        if (surah != null) {
            selectSurah(surah)
        }
    }

    fun selectPage(page: Int) {
        val boundedPage = page.coerceIn(1, 604)
        val pageStart = _pageStarts.value.find { it.page == boundedPage }
        val surah = _surahs.value.find { it.number == pageStart?.surahNumber }
        if (surah != null) {
            _selectedSurah.value = surah
            _selectedPage.value = boundedPage
        }
    }

    fun clearSelection() {
        _selectedSurah.value = null
        _selectedPage.value = null
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun saveBookmark(surahNumber: Int, ayahNumber: Int) {
        val sharedPref = getApplication<Application>().getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putInt("last_read_surah", surahNumber)
            .putInt("last_read_ayah", ayahNumber)
            .apply()
        _bookmarkSurah.value = surahNumber
        _bookmarkAyah.value = ayahNumber
    }

    fun updateFontSize(newSize: Float) {
        _readerFontSize.value = newSize
        val sharedPref = getApplication<Application>().getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putFloat("reader_font_size", newSize).apply()
    }

    private fun loadBookmark() {
        val sharedPref = getApplication<Application>().getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
        _bookmarkSurah.value = sharedPref.getInt("last_read_surah", 0)
        _bookmarkAyah.value = sharedPref.getInt("last_read_ayah", 0)
    }

    private fun loadReaderFontSize() {
        val sharedPref = getApplication<Application>().getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)
        _readerFontSize.value = sharedPref.getFloat("reader_font_size", 24f)
    }

    fun loadTranslations(context: Context, surahNumber: Int, lang: String) {
        if (lang == "en" || lang == "ar") {
            _translatedAyahs.value = emptyMap()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val list = com.yehia.prayertimes.utils.QuranTranslationManager.getTranslation(context, surahNumber, lang)
            if (list != null) {
                val map = list.mapIndexed { index, text -> (index + 1) to text }.toMap()
                _translatedAyahs.value = map
            } else {
                _translatedAyahs.value = emptyMap()
            }
        }
    }
}
