@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
package com.yehia.prayertimes.ui.screens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.data.Ayah
import com.yehia.prayertimes.data.Surah
import com.yehia.prayertimes.data.QuranPageAyah
import com.yehia.prayertimes.data.StandardQuranPage
import com.yehia.prayertimes.ui.theme.SalamIconBadge
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.ThemePalette
import com.yehia.prayertimes.utils.LanguageManager
import com.yehia.prayertimes.ui.theme.readingThemes
import com.yehia.prayertimes.ui.viewmodel.QuranViewModel
import com.yehia.prayertimes.ui.theme.salamClickable
import kotlinx.coroutines.launch

private data class QuranReciter(
    val shortName: String,
    val displayName: String,
    val directory: String,
    val quality: String
)

private val quranReciters = listOf(
    QuranReciter("Alafasy", "Mishary Alafasy", "Alafasy_64kbps", "64 kbps"),
    QuranReciter("Ghamadi", "Saad Al-Ghamadi", "Ghamadi_40kbps", "40 kbps"),
    QuranReciter("Sudais", "Abdul Rahman As-Sudais", "Abdurrahmaan_As-Sudais_64kbps", "64 kbps"),
    QuranReciter("Husary", "Mahmoud Khalil Al-Husary", "Husary_64kbps", "64 kbps"),
    QuranReciter("Abdul Basit", "Abdul Basit Murattal", "Abdul_Basit_Murattal_64kbps", "64 kbps"),
    QuranReciter("Shatri", "Abu Bakr Ash-Shatri", "Abu_Bakr_Ash-Shaatree_64kbps", "64 kbps"),
    QuranReciter("Hani Rifai", "Hani Ar-Rifai", "Hani_Rifai_64kbps", "64 kbps")
)

@Composable
fun QuranDetailScreen(
    viewModel: QuranViewModel
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val surah by viewModel.selectedSurah.collectAsState()
    val selectedPage by viewModel.selectedPage.collectAsState()
    val allSurahs by viewModel.surahs.collectAsState()
    val pageStarts by viewModel.pageStarts.collectAsState()
    val standardPages by viewModel.standardPages.collectAsState()
    val bookmarkSurah by viewModel.bookmarkSurah.collectAsState()
    val bookmarkAyah by viewModel.bookmarkAyah.collectAsState()
    val readerFontSize by viewModel.readerFontSize.collectAsState()

    val context = LocalContext.current
    val currentLang by LanguageManager.currentLang
    val translatedAyahs by viewModel.translatedAyahs.collectAsState()

    LaunchedEffect(surah?.number, currentLang) {
        surah?.let {
            viewModel.loadTranslations(context, it.number, currentLang)
        }
    }

    val prefs = remember { context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE) }
    var selectedPaperBg by remember { mutableStateOf(prefs.getString("quran_read_bg", "parchment") ?: "parchment") }
    var readingMode by remember { mutableStateOf(prefs.getString("quran_reading_mode", "scroll") ?: "scroll") }

    // Reading Background Colors from ReadingTheme
    val activeReadingTheme = remember(selectedPaperBg) {
        val lookup = if (selectedPaperBg == "cream") "Cream" else selectedPaperBg
        readingThemes.find { it.name.lowercase() == lookup.lowercase() } ?: readingThemes.first()
    }
    val paperBgColor = activeReadingTheme.background
    val textPrimaryColor = activeReadingTheme.textPrimary
    val textSecondaryColor = activeReadingTheme.textSecondary
    val cardBgColor = activeReadingTheme.surface
    val strokeColor = activeReadingTheme.accent

    var showPreferences by remember { mutableStateOf(false) }
    var showAudioControls by remember { mutableStateOf(false) }
    var showSurahJumpDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current

    // Audio Reciter State
    val mediaPlayer = remember { MediaPlayer() }
    var currentPlayingAyahIndex by remember { mutableStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var isAudioBuffering by remember { mutableStateOf(false) }
    var activeReciter by remember {
        mutableStateOf(
            prefs.getString("quran_active_reciter", quranReciters.first().directory)
                ?.let { saved -> if (saved == "Mishary_Al-Afasy_64kbps") "Alafasy_64kbps" else saved }
                ?: quranReciters.first().directory
        )
    }
    val activeReciterInfo = quranReciters.find { it.directory == activeReciter } ?: quranReciters.first()

    if (surah == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(paperBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text("No Surah Selected", color = textPrimaryColor)
        }
        return
    }

    val activeSurah = surah!!



    fun standardPageFor(surahNumber: Int, ayahNumber: Int): Int {
        if (pageStarts.isEmpty()) return 1
        return pageStarts.lastOrNull {
            it.surahNumber < surahNumber || (it.surahNumber == surahNumber && it.ayahNumber <= ayahNumber)
        }?.page ?: 1
    }

    // Play Quran Audio logic
    fun playAyah(index: Int) {
        if (index < 0 || index >= activeSurah.ayahs.size) return
        val ayah = activeSurah.ayahs[index]
        val formattedSurah = "%03d".format(activeSurah.number)
        val formattedAyah = "%03d".format(ayah.number)
        val url = "https://everyayah.com/data/$activeReciter/$formattedSurah$formattedAyah.mp3"

        try {
            currentPlayingAyahIndex = index
            isAudioBuffering = true
            isPlaying = false
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                it.start()
                isAudioBuffering = false
                isPlaying = true
                currentPlayingAyahIndex = index
            }
            mediaPlayer.setOnCompletionListener {
                if (index < activeSurah.ayahs.size - 1) {
                    val nextIdx = index + 1
                    playAyah(nextIdx)
                } else {
                    isPlaying = false
                    isAudioBuffering = false
                    currentPlayingAyahIndex = -1
                }
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                Toast.makeText(context, "Failed to stream audio recitation", Toast.LENGTH_SHORT).show()
                isPlaying = false
                isAudioBuffering = false
                currentPlayingAyahIndex = -1
                false
            }
        } catch (e: Exception) {
            isPlaying = false
            isAudioBuffering = false
            currentPlayingAyahIndex = -1
        }
    }

    // Safety: Reset audio player states and stop playback if surah changes
    LaunchedEffect(activeSurah.number) {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
        } catch (e: Exception) {}
        isPlaying = false
        currentPlayingAyahIndex = -1

        // Auto-scroll to bookmark on loading this Surah in Scroll mode
        if (readingMode == "scroll") {
            if (bookmarkSurah == activeSurah.number && bookmarkAyah > 0) {
                val headerOffset = if (activeSurah.number != 9) 2 else 1 // include Audio Control item
                val targetIndex = (bookmarkAyah - 1 + headerOffset).coerceAtLeast(0)
                coroutineScope.launch {
                    listState.scrollToItem(targetIndex)
                }
            } else {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                }
            }
        }
    }

    // Audio tracking LaunchedEffect to smoothly scroll the active verse into view (Scroll mode)
    LaunchedEffect(currentPlayingAyahIndex) {
        if (readingMode == "scroll" && currentPlayingAyahIndex >= 0) {
            val headerOffset = if (activeSurah.number != 9) 2 else 1
            val targetScrollIndex = currentPlayingAyahIndex + headerOffset
            if (targetScrollIndex < listState.layoutInfo.totalItemsCount) {
                listState.animateScrollToItem(targetScrollIndex)
            }
        }
    }

    // Release player on screen dispose
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer.stop()
                mediaPlayer.release()
            } catch (e: Exception) {}
        }
    }

    SalamScreenScaffold(
        showGeometricPattern = false,
        backgroundBrush = SolidColor(paperBgColor)
    ) {
        // Custom Styled TopBar for reading screen to respect reader themes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = SalamSpacing.elementGap),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { viewModel.clearSelection() },
                    modifier = Modifier.size(SalamSpacing.touchTarget)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimaryColor,
                        modifier = Modifier.size(SalamSpacing.iconSize)
                    )
                }
                Spacer(modifier = Modifier.width(SalamSpacing.elementGap))
                Column(
                    modifier = Modifier.salamClickable { showSurahJumpDialog = true }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activeSurah.nameEnglish,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimaryColor
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Switch Surah",
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = activeSurah.nameTranslation,
                        style = MaterialTheme.typography.labelSmall.copy(color = textSecondaryColor)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { 
                    showAudioControls = !showAudioControls 
                    if (showAudioControls) showPreferences = false
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Audio Reciter",
                        tint = if (showAudioControls) palette.primary else textPrimaryColor
                    )
                }
                Spacer(modifier = Modifier.width(SalamSpacing.elementGap))
                IconButton(onClick = { 
                    showPreferences = !showPreferences 
                    if (showPreferences) showAudioControls = false
                }) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Reading Settings",
                        tint = if (showPreferences) palette.primary else textPrimaryColor
                    )
                }
                Spacer(modifier = Modifier.width(SalamSpacing.elementGap))
                Text(
                    text = activeSurah.nameArabic,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = palette.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .salamClickable { showSurahJumpDialog = true }
                )
            }
        }

        // Animated Preferences Drawer Card
        AnimatedVisibility(
            visible = showPreferences,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.cardGap),
                shape = SalamShapes.cardMedium,
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                border = BorderStroke(1.dp, strokeColor.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SalamSpacing.cardPaddingInner)
                ) {
                    // Font Size Preference
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Arabic Font Size",
                            color = textPrimaryColor,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${readerFontSize.toInt()} sp",
                            color = palette.primary,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = readerFontSize,
                        onValueChange = { viewModel.updateFontSize(it) },
                        valueRange = 16f..40f,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.primary,
                            activeTrackColor = palette.primary,
                            inactiveTrackColor = strokeColor.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = strokeColor.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Reading Mode Preference
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reading Mode",
                            color = textPrimaryColor,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("scroll" to "Scroll", "page" to "Page").forEach { (mode, label) ->
                                val isSel = readingMode == mode
                                Box(
                                    modifier = Modifier
                                        .clip(SalamShapes.cardSmall)
                                        .background(if (isSel) palette.primary.copy(alpha = 0.2f) else strokeColor.copy(alpha = 0.15f))
                                        .salamClickable {
                                            readingMode = mode
                                            prefs.edit().putString("quran_reading_mode", mode).apply()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) palette.primary else textSecondaryColor
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Standard Mushaf Page Configuration
                    AnimatedVisibility(
                        visible = readingMode == "page",
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Pages,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Standard Madani Mushaf",
                                        color = textPrimaryColor,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    text = "604 pages",
                                    color = textSecondaryColor,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = strokeColor.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Reading Paper Color Theme
                    Text(
                        text = "Reading Paper Theme",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimaryColor
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "parchment" to "Parchment",
                            "emerald" to "Emerald",
                            "night" to "Night",
                            "cream" to "Soft Cream"
                        ).forEach { (code, name) ->
                            val isSel = selectedPaperBg == code
                            Box(
                                modifier = Modifier
                                    .clip(SalamShapes.cardSmall)
                                    .background(if (isSel) palette.primary.copy(alpha = 0.2f) else strokeColor.copy(alpha = 0.15f))
                                    .salamClickable {
                                        selectedPaperBg = code
                                        prefs.edit().putString("quran_read_bg", code).apply()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) palette.primary else textSecondaryColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Animated Audio Controls Drawer Card
        AnimatedVisibility(
            visible = showAudioControls,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.cardGap),
                shape = SalamShapes.cardMedium,
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                border = BorderStroke(1.dp, strokeColor.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.cardPaddingInner)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    mediaPlayer.pause()
                                    isPlaying = false
                                    isAudioBuffering = false
                                } else {
                                    if (currentPlayingAyahIndex == -1) {
                                        currentPlayingAyahIndex = 0
                                    }
                                    playAyah(currentPlayingAyahIndex)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = textPrimaryColor
                            )
                        }
                        IconButton(
                            onClick = {
                                mediaPlayer.stop()
                                mediaPlayer.reset()
                                isPlaying = false
                                isAudioBuffering = false
                                currentPlayingAyahIndex = -1
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = textPrimaryColor
                            )
                        }
                        Spacer(modifier = Modifier.width(SalamSpacing.elementGap))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    isAudioBuffering && currentPlayingAyahIndex >= 0 -> "Loading Ayah ${currentPlayingAyahIndex + 1}"
                                    isPlaying && currentPlayingAyahIndex >= 0 -> "Ayah ${currentPlayingAyahIndex + 1} • ${activeReciterInfo.shortName}"
                                    else -> "Ready • ${activeReciterInfo.displayName}"
                                },
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textPrimaryColor
                                )
                            )
                            Text(
                                text = "Highlight follows the active recitation",
                                style = MaterialTheme.typography.labelSmall.copy(color = textSecondaryColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quranReciters.forEach { reciter ->
                            val isSel = activeReciter == reciter.directory
                            Box(
                                modifier = Modifier
                                    .clip(SalamShapes.pill)
                                    .background(if (isSel) palette.primary.copy(alpha = 0.2f) else strokeColor.copy(alpha = 0.15f))
                                    .salamClickable {
                                        activeReciter = reciter.directory
                                        prefs.edit().putString("quran_active_reciter", reciter.directory).apply()
                                        if (isPlaying || isAudioBuffering) {
                                            playAyah(currentPlayingAyahIndex)
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = reciter.shortName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) palette.primary else textSecondaryColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (readingMode == "page") {
            val initialPage = remember(selectedPage, bookmarkSurah, bookmarkAyah, pageStarts) {
                when {
                    selectedPage != null -> selectedPage!!.coerceIn(1, 604)
                    bookmarkSurah > 0 && bookmarkAyah > 0 -> standardPageFor(bookmarkSurah, bookmarkAyah)
                    else -> standardPageFor(activeSurah.number, 1)
                } - 1
            }
            val pagerState = key(pageStarts.size) {
                rememberPagerState(
                    initialPage = initialPage.coerceIn(0, (standardPages.size - 1).coerceAtLeast(0)),
                    pageCount = { standardPages.size.coerceAtLeast(1) }
                )
            }

            LaunchedEffect(pagerState.currentPage, standardPages) {
                val currentPage = standardPages.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
                val firstAyah = currentPage.ayahs.firstOrNull() ?: return@LaunchedEffect
                
                // Auto-update surah name in top left if we cross into a new surah in page mode
                if (activeSurah.number != firstAyah.surah.number) {
                    viewModel.selectSurah(firstAyah.surah)
                }

                if (bookmarkSurah != firstAyah.surah.number || bookmarkAyah != firstAyah.ayah.number) {
                    viewModel.saveBookmark(firstAyah.surah.number, firstAyah.ayah.number)
                }
            }

            LaunchedEffect(activeSurah.number) {
                val targetPage = standardPageFor(activeSurah.number, 1) - 1
                if (targetPage in 0 until pagerState.pageCount && targetPage != pagerState.currentPage) {
                    pagerState.scrollToPage(targetPage)
                }
            }

            LaunchedEffect(currentPlayingAyahIndex) {
                if (currentPlayingAyahIndex >= 0) {
                    val targetPage = standardPageFor(activeSurah.number, currentPlayingAyahIndex + 1) - 1
                    if (targetPage in 0 until pagerState.pageCount && targetPage != pagerState.currentPage) {
                        pagerState.animateScrollToPage(targetPage)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    pageSpacing = 14.dp,
                    beyondBoundsPageCount = 1
                ) { pageIndex ->
                    val page = standardPages.getOrNull(pageIndex)
                    if (page == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Loading Quran pages...", color = textSecondaryColor)
                        }
                    } else {
                        QuranStandardPageView(
                            page = page,
                            activeSurahNumber = activeSurah.number,
                            activePlayingAyahNumber = if (currentPlayingAyahIndex >= 0) currentPlayingAyahIndex + 1 else -1,
                            bookmarkSurah = bookmarkSurah,
                            bookmarkAyah = bookmarkAyah,
                            fontSize = readerFontSize,
                            palette = palette,
                            cardBgColor = cardBgColor,
                            strokeColor = strokeColor,
                            textPrimaryColor = textPrimaryColor,
                            textSecondaryColor = textSecondaryColor,
                            onCopy = { pageAyah ->
                                val copyText = "\"${pageAyah.ayah.textArabic}\"\n\nTranslation: ${translatedAyahs[pageAyah.ayah.number] ?: pageAyah.ayah.textEnglish}\n\n[Surah ${pageAyah.surah.nameEnglish} (${pageAyah.surah.number}:${pageAyah.ayah.number})]"
                                clipboard.setText(AnnotatedString(copyText))
                            },
                            onBookmark = { pageAyah ->
                                viewModel.saveBookmark(pageAyah.surah.number, pageAyah.ayah.number)
                            },
                            translatedAyahs = translatedAyahs
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        enabled = pagerState.currentPage > 0,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
                    ) {
                        Text("Previous", color = if (pagerState.currentPage > 0) palette.primary else textSecondaryColor)
                    }
                    Text(
                        text = "Page ${pagerState.currentPage + 1} / 604",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimaryColor
                        )
                    )
                    TextButton(
                        enabled = pagerState.currentPage < pagerState.pageCount - 1,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
                    ) {
                        Text("Next", color = if (pagerState.currentPage < pagerState.pageCount - 1) palette.primary else textSecondaryColor)
                    }
                }
            }
        } else {
            // Traditional Scroll mode
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
            ) {
                // Surah Info Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = SalamShapes.cardMedium,
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        border = BorderStroke(1.dp, strokeColor.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SalamSpacing.cardPaddingInner),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Surah ${activeSurah.number}",
                                color = palette.primary,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeSurah.nameEnglish,
                                color = textPrimaryColor,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "${activeSurah.revelationType} • ${activeSurah.ayahCount} Ayahs",
                                color = textSecondaryColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }



                // Bismillah Card (Except Surah 9)
                if (activeSurah.number != 9) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = SalamSpacing.elementGap),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Ayahs list with keying
                itemsIndexed(activeSurah.ayahs, key = { _, ayah -> ayah.number }) { index, ayah ->
                    val isBookmarked = bookmarkSurah == activeSurah.number && bookmarkAyah == ayah.number
                    val isReciting = currentPlayingAyahIndex == index

                    AyahCard(
                        ayah = ayah,
                        surahNumber = activeSurah.number,
                        fontSize = readerFontSize,
                        isBookmarked = isBookmarked,
                        isReciting = isReciting,
                        palette = palette,
                        containerColor = if (isReciting) palette.primary.copy(alpha = 0.12f) else cardBgColor,
                        strokeColor = if (isReciting) palette.primary else strokeColor,
                        textPrimaryColor = textPrimaryColor,
                        textSecondaryColor = textSecondaryColor,
                        onCopy = {
                            val copyText = "\"${ayah.textArabic}\"\n\nTranslation: ${translatedAyahs[ayah.number] ?: ayah.textEnglish}\n\n[Surah ${activeSurah.nameEnglish} (${activeSurah.number}:${ayah.number})]"
                            clipboard.setText(AnnotatedString(copyText))
                        },
                        onBookmark = {
                            viewModel.saveBookmark(activeSurah.number, ayah.number)
                        },
                        translation = translatedAyahs[ayah.number]
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
                }
            }
        }
    }

    // Jump to Surah Dialog
    if (showSurahJumpDialog) {
        AlertDialog(
            onDismissRequest = { showSurahJumpDialog = false },
            title = {
                Text(
                    text = "Jump to Surah",
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor
                )
            },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allSurahs, key = { it.number }) { item ->
                            val isCurrent = item.number == activeSurah.number
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SalamShapes.cardMedium)
                                    .background(
                                        if (isCurrent) palette.primary.copy(alpha = 0.25f) else strokeColor.copy(
                                            alpha = 0.2f
                                        )
                                    )
                                    .salamClickable {
                                        viewModel.selectSurah(item)
                                        showSurahJumpDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                if (isCurrent) palette.primary else strokeColor.copy(alpha = 0.4f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.number.toString(),
                                            color = if (isCurrent) palette.background else textPrimaryColor,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.nameEnglish,
                                            color = textPrimaryColor,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${item.ayahCount} Ayahs",
                                            color = textSecondaryColor,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                Text(
                                    text = item.nameArabic,
                                    color = if (isCurrent) palette.primary else textPrimaryColor,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSurahJumpDialog = false }) {
                    Text("Close", color = palette.primary)
                }
            },
            containerColor = cardBgColor
        )
    }
}



@Composable
private fun QuranStandardPageView(
    page: StandardQuranPage,
    activeSurahNumber: Int,
    activePlayingAyahNumber: Int,
    bookmarkSurah: Int,
    bookmarkAyah: Int,
    fontSize: Float,
    palette: ThemePalette,
    cardBgColor: Color,
    strokeColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onCopy: (QuranPageAyah) -> Unit,
    onBookmark: (QuranPageAyah) -> Unit,
    translatedAyahs: Map<Int, String>
) {
    var selectedPageAyah by remember { mutableStateOf<QuranPageAyah?>(null) }

    fun getDisplayArabicText(ayah: Ayah, surahNum: Int): String {
        val bismillahVariants = listOf(
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
            "بِسْمِ اللهِ الرَّحْمَنِ الرَّحِيمِ"
        )
        val isFirstAyahBismillah = ayah.number == 1 &&
                surahNum != 1 &&
                surahNum != 9 &&
                bismillahVariants.any { ayah.textArabic.startsWith(it) }
        return if (isFirstAyahBismillah) {
            var cleaned = ayah.textArabic
            bismillahVariants.forEach { variant -> cleaned = cleaned.replace(variant, "") }
            cleaned.trim().ifEmpty { ayah.textArabic }
        } else {
            ayah.textArabic
        }
    }

    val surahChunks = remember(page.ayahs) {
        val chunks = mutableListOf<Pair<Surah, List<QuranPageAyah>>>()
        var currentSurah: Surah? = null
        var currentList = mutableListOf<QuranPageAyah>()
        for (ayah in page.ayahs) {
            if (currentSurah == null || currentSurah.number != ayah.surah.number) {
                if (currentList.isNotEmpty() && currentSurah != null) {
                    chunks.add(currentSurah to currentList)
                    currentList = mutableListOf()
                }
                currentSurah = ayah.surah
            }
            currentList.add(ayah)
        }
        if (currentList.isNotEmpty() && currentSurah != null) {
            chunks.add(currentSurah to currentList)
        }
        chunks
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            border = BorderStroke(1.dp, strokeColor.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .border(BorderStroke(1.5.dp, palette.primary.copy(alpha = 0.7f)), RoundedCornerShape(6.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Page ${page.pageNumber}",
                    color = textSecondaryColor,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                surahChunks.forEach { (surah, ayahs) ->
                    if (surah.number != 9 && ayahs.firstOrNull()?.ayah?.number == 1) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    val annotatedString = buildAnnotatedString {
                        ayahs.forEach { pageAyah ->
                            val start = length
                            val text = getDisplayArabicText(pageAyah.ayah, pageAyah.surah.number)
                            append(text)
                            val end = length

                            val isReciting = pageAyah.surah.number == activeSurahNumber && pageAyah.ayah.number == activePlayingAyahNumber
                            if (isReciting) {
                                addStyle(SpanStyle(background = palette.primary.copy(alpha = 0.2f)), start, end)
                            }

                            addStringAnnotation(tag = "AYAH_CLICK", annotation = page.ayahs.indexOf(pageAyah).toString(), start = start, end = end)

                            pushStyle(SpanStyle(color = palette.primary, fontWeight = FontWeight.Bold))
                            append(" ﴿${pageAyah.ayah.number}﴾ ")
                            pop()
                        }
                    }

                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimaryColor,
                            textAlign = TextAlign.Center,
                            textDirection = TextDirection.Rtl,
                            lineHeight = (fontSize * 1.8f).sp
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = "AYAH_CLICK", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    val index = annotation.item.toIntOrNull()
                                    if (index != null) {
                                        selectedPageAyah = page.ayahs.getOrNull(index)
                                    }
                                }
                        }
                    )
                }
            }
        }
    }

    selectedPageAyah?.let { pageAyah ->
        val isBookmarked = bookmarkSurah == pageAyah.surah.number && bookmarkAyah == pageAyah.ayah.number
        AlertDialog(
            onDismissRequest = { selectedPageAyah = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${pageAyah.surah.nameEnglish} • Ayah ${pageAyah.ayah.number}",
                        fontWeight = FontWeight.Bold,
                        color = textPrimaryColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = {
                            onBookmark(pageAyah)
                        }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) palette.primary else textSecondaryColor
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = pageAyah.ayah.textArabic,
                        fontSize = (fontSize - 2).coerceAtLeast(16f).sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimaryColor,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        style = LocalTextStyle.current.copy(
                            textDirection = TextDirection.Rtl,
                            lineHeight = (fontSize * 1.5f).sp
                        )
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(strokeColor.copy(alpha = 0.3f)))
                    Text(
                        text = translatedAyahs[pageAyah.ayah.number] ?: pageAyah.ayah.textEnglish,
                        style = MaterialTheme.typography.bodyMedium.copy(color = textSecondaryColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onCopy(pageAyah)
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", color = palette.primary)
                        }
                    }
                    TextButton(onClick = { selectedPageAyah = null }) {
                        Text("Close", color = palette.primary)
                    }
                }
            },
            containerColor = cardBgColor
        )
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    surahNumber: Int,
    fontSize: Float,
    isBookmarked: Boolean,
    isReciting: Boolean,
    palette: ThemePalette,
    containerColor: Color,
    strokeColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    onCopy: () -> Unit,
    onBookmark: () -> Unit,
    translation: String? = null
) {
    // Memoize bismillah detection — only runs when ayah or surahNumber changes
    val displayArabicText = remember(ayah.number, surahNumber, ayah.textArabic) {
        val bismillahVariants = listOf(
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
            "بِسْمِ اللهِ الرَّحْمَنِ الرَّحِيمِ"
        )
        val isFirstAyahBismillah = ayah.number == 1 &&
                surahNumber != 1 &&
                surahNumber != 9 &&
                bismillahVariants.any { ayah.textArabic.startsWith(it) }
        if (isFirstAyahBismillah) {
            var cleaned = ayah.textArabic
            bismillahVariants.forEach { variant -> cleaned = cleaned.replace(variant, "") }
            cleaned.trim().ifEmpty { ayah.textArabic }
        } else {
            ayah.textArabic
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SalamShapes.cardMedium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, strokeColor.copy(alpha = if (isReciting) 0.8f else 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SalamSpacing.cardPaddingInner)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verse Indicator
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(strokeColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ayah.number.toString(),
                        color = palette.primary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Interactive Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .salamClickable(onClick = onCopy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Verse",
                            tint = textSecondaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .salamClickable(onClick = onBookmark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Verse",
                            tint = if (isBookmarked) palette.primary else textSecondaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

            // Arabic Text (linked to custom font size)
            Text(
                text = displayArabicText,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimaryColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                style = LocalTextStyle.current.copy(
                    textDirection = TextDirection.Rtl,
                    lineHeight = (fontSize * 1.6f).sp
                )
            )

            if (LanguageManager.currentLang.value != "ar") {
                Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

                // Localized/Default Translation
                Text(
                    text = translation ?: ayah.textEnglish,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondaryColor,
                        lineHeight = 20.sp
                    ),
                    textAlign = if (LanguageManager.isRtl()) TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
