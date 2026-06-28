package com.yehia.prayertimes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.data.Hadith
import com.yehia.prayertimes.data.HadithData
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamTopBar
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable
import java.util.Calendar

@Composable
fun HadithScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val hadiths = HadithData.hadiths
    val clipboardManager = LocalClipboardManager.current

    // Safe calendar calculation mapping today safely to 1-42
    val todayDay = remember {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val targetIndex = (day % hadiths.size) + 1
        targetIndex.coerceIn(1, hadiths.size)
    }

    var currentId by remember { mutableStateOf(todayDay) }
    val activeHadith = remember(currentId) {
        hadiths.find { it.id == currentId } ?: hadiths.first()
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredHadiths = remember(searchQuery) {
        val q = searchQuery.lowercase()
        hadiths.filter {
            it.english.lowercase().contains(q) ||
            it.arabic.contains(q) ||
            it.narrator.lowercase().contains(q) ||
            it.source.lowercase().contains(q)
        }
    }

    // Keep track of expanded list items (by ID)
    var expandedHadithId by remember { mutableStateOf(-1) }

    SalamScreenScaffold(
        showGeometricPattern = false
    ) {
        SalamTopBar(
            title = "Sahih Hadith",
            onNavigateBack = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
        ) {
            // Item 1: Search Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it 
                        // Close any open expanded card during search to avoid jumps
                        expandedHadithId = -1
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = SalamSpacing.elementGap),
                    placeholder = { Text("Search verified hadiths...", color = palette.textMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = palette.primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = palette.textSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = SalamShapes.pill,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.cardElevation2,
                        unfocusedContainerColor = palette.cardElevation1,
                        focusedBorderColor = palette.primary,
                        unfocusedBorderColor = palette.outline.copy(alpha = 0.35f),
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary
                    )
                )
            }

            if (searchQuery.isBlank()) {
                // Item 2: Daily Hadith Swipeable Banner
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Daily Hadith",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                        )
                        
                        SalamCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 2,
                            shape = SalamShapes.squircle
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SalamSpacing.cardPaddingInner),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Hadith $currentId of ${hadiths.size}",
                                        color = palette.primary,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )

                                    IconButton(
                                        onClick = {
                                            val copyText = "\"${activeHadith.arabic}\"\n\nTranslation: ${activeHadith.english}\n\nNarrated by: ${activeHadith.narrator}\nSource: [${activeHadith.source}]"
                                            clipboardManager.setText(AnnotatedString(copyText))
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Hadith",
                                            tint = palette.textSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Arabic text (Scrollable inside the card if large)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 140.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = activeHadith.arabic,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = LocalTextStyle.current.copy(
                                            textDirection = TextDirection.Rtl,
                                            lineHeight = 28.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // English Translation
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 110.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = activeHadith.english,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = palette.textSecondary,
                                            lineHeight = 18.sp
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Narrator & Source Info
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Narrated by ${activeHadith.narrator}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = palette.primary
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "[${activeHadith.source}]",
                                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Banner swipe controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    currentId = if (currentId == 1) hadiths.size else currentId - 1
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White, CircleShape)
                                    .border(0.5.dp, palette.outline.copy(alpha = 0.35f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Hadith",
                                    tint = palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (currentId == todayDay) {
                                Text(
                                    text = "Today's selection",
                                    color = palette.textMuted,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            } else {
                                TextButton(
                                    onClick = { currentId = todayDay },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Reset to Today", color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            IconButton(
                                onClick = {
                                    currentId = if (currentId == hadiths.size) 1 else currentId + 1
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White, CircleShape)
                                    .border(0.5.dp, palette.outline.copy(alpha = 0.35f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Hadith",
                                    tint = palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Item 3: Verified Hadith List Section Header
                item {
                    Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                    Text(
                        text = "Verified Ahadith Collection",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Item 4+: All verified Ahadith (Expandable Cards)
                itemsIndexed(hadiths, key = { _, it -> it.id }) { index, hadith ->
                    ExpandableHadithCard(
                        hadith = hadith,
                        isExpanded = expandedHadithId == hadith.id,
                        index = index,
                        palette = palette,
                        onToggleExpand = {
                            expandedHadithId = if (expandedHadithId == hadith.id) -1 else hadith.id
                        },
                        onCopy = {
                            val copyText = "\"${hadith.arabic}\"\n\nTranslation: ${hadith.english}\n\nNarrated by: ${hadith.narrator}\nSource: [${hadith.source}]"
                            clipboardManager.setText(AnnotatedString(copyText))
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
                }
            } else {
                // ─── SEARCH MODE ───
                if (filteredHadiths.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No matching verified hadiths found.", color = palette.textSecondary)
                        }
                    }
                } else {
                    itemsIndexed(filteredHadiths, key = { _, it -> it.id }) { index, hadith ->
                        ExpandableHadithCard(
                            hadith = hadith,
                            isExpanded = expandedHadithId == hadith.id,
                            index = index,
                            palette = palette,
                            onToggleExpand = {
                                expandedHadithId = if (expandedHadithId == hadith.id) -1 else hadith.id
                            },
                            onCopy = {
                                val copyText = "\"${hadith.arabic}\"\n\nTranslation: ${hadith.english}\n\nNarrated by: ${hadith.narrator}\nSource: [${hadith.source}]"
                                clipboardManager.setText(AnnotatedString(copyText))
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableHadithCard(
    hadith: Hadith,
    isExpanded: Boolean,
    index: Int,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    onToggleExpand: () -> Unit,
    onCopy: () -> Unit
) {

    val shape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
    SalamCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggleExpand,
        elevation = if (isExpanded) 3 else 2,
        shape = shape
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hadith.id.toString(),
                            color = palette.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sahih Hadith",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isExpanded) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Hadith",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand details",
                        tint = palette.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!isExpanded) {
                // Folded Preview
                Text(
                    text = hadith.english,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = palette.textSecondary,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "— Narrated by ${hadith.narrator} [${hadith.source}]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = palette.textMuted,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Expanded Full details with vertical animation
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Arabic
                        Text(
                            text = hadith.arabic,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            style = LocalTextStyle.current.copy(
                                textDirection = TextDirection.Rtl,
                                lineHeight = 30.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // English
                        Text(
                            text = hadith.english,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = palette.textSecondary,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalDivider(color = palette.outline.copy(alpha = 0.25f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Narrator & source
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Narrated by: ${hadith.narrator}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "[${hadith.source}]",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = palette.textMuted,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
