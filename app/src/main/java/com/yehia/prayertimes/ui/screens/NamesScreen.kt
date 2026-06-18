package com.yehia.prayertimes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.data.AllahName
import com.yehia.prayertimes.data.NamesData
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamTopBar
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamesScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val names = NamesData.names
    val clipboard = LocalClipboardManager.current

    var selectedName by remember { mutableStateOf<AllahName?>(null) }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Names of Allah",
            onNavigateBack = onNavigateBack
        )

        Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(names, key = { _, name -> name.id }) { index, name ->
                NameItemCard(name = name, index = index, palette = palette, onClick = { selectedName = name })
            }
        }
    }

    // Detail Dialog
    if (selectedName != null) {
        val item = selectedName!!
        AlertDialog(
            onDismissRequest = { selectedName = null },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.id.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                    Text(
                        text = item.arabic,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = item.transliteration,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = palette.textSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))
                    Text(
                        text = "Quranic Occurrence: Surah ${item.reference}",
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        val textToCopy = "${item.arabic} (${item.transliteration}) - ${item.translation} [Quran ${item.reference}]"
                        clipboard.setText(AnnotatedString(textToCopy))
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = palette.primary)
                    }
                    TextButton(onClick = { selectedName = null }) {
                        Text("Close", color = palette.primary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = palette.surface,
            shape = SalamShapes.squircle
        )
    }
}

@Composable
fun NameItemCard(
    name: AllahName,
    index: Int,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    onClick: () -> Unit
) {

    val shape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2

    SalamCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = 2,
        onClick = onClick,
        shape = shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SalamSpacing.elementGap),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(palette.surfaceVariant.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.id.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = palette.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name.arabic,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = name.transliteration,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
