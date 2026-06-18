package com.yehia.prayertimes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yehia.prayertimes.data.Surah
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamIconBadge
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSectionHeader
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.viewmodel.QuranViewModel
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranListScreen(
    viewModel: QuranViewModel
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val surahs by viewModel.filteredSurahs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val bookmarkSurah by viewModel.bookmarkSurah.collectAsState()
    val bookmarkAyah by viewModel.bookmarkAyah.collectAsState()

    SalamScreenScaffold {
        // Screen Title
        SalamSectionHeader(title = "Holy Quran")

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearch(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SalamSpacing.cardGap),
            placeholder = {
                Text(
                    "Search Surah name or number...",
                    color = palette.textSecondary
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = palette.primary
                )
            },
            singleLine = true,
            shape = SalamShapes.pill,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.primary,
                unfocusedBorderColor = palette.outline,
                focusedLabelColor = palette.primary,
                cursorColor = palette.primary,
                focusedContainerColor = palette.surface,
                unfocusedContainerColor = palette.surface,
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary
            )
        )

        // Verse-level Bookmark Resume Banner
        if (bookmarkSurah > 0) {
            val allSurahs by viewModel.surahs.collectAsState()
            val bookmarkedSurah = allSurahs.find { it.number == bookmarkSurah }
            if (bookmarkedSurah != null) {
                SalamCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SalamSpacing.cardPaddingInner),
                    elevation = 3,
                    isActive = true,
                    shape = SalamShapes.squircle,
                    onClick = { viewModel.selectSurah(bookmarkedSurah) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SalamSpacing.cardPaddingInner),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SalamIconBadge {
                            Icon(
                                Icons.Default.Book,
                                contentDescription = "Bookmark",
                                tint = palette.primary,
                                modifier = Modifier.size(SalamSpacing.iconSize)
                            )
                        }
                        Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
                        Column {
                            Text(
                                text = "Resume Reading",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Surah ${bookmarkedSurah.nameEnglish} • Verse $bookmarkAyah",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = "Click to jump back in",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.textMuted
                                )
                            )
                        }
                    }
                }
            }
        }

        // Surahs List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(surahs, key = { _, it -> it.number }) { index, surah ->
                SurahItemCard(surah = surah, index = index, palette = palette, onClick = { viewModel.selectSurah(surah) })
            }
        }
    }
}

@Composable
fun SurahItemCard(
    surah: Surah,
    index: Int,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    onClick: () -> Unit
) {
    val shape = SalamShapes.expressiveCorner2
    SalamCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2,
        shape = shape,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SalamSpacing.cardPaddingInner),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Number Badge
                SalamIconBadge(
                    size = 40.dp
                ) {
                    Text(
                        text = surah.number.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = palette.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))

                // English Name & Details
                Column {
                    Text(
                        text = surah.nameEnglish,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = surah.revelationType,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.textSecondary
                            )
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.textMuted
                            )
                        )
                        Text(
                            text = "${surah.ayahCount} Ayahs",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.textSecondary
                            )
                        )
                    }
                }
            }

            // Arabic Name
            Text(
                text = surah.nameArabic,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = palette.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
