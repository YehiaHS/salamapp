package com.yehia.prayertimes.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.data.HajjData
import com.yehia.prayertimes.data.HajjStep
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
fun HajjUmrahScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val prefs = remember { context.getSharedPreferences("salam_prefs", Context.MODE_PRIVATE) }

    var activeTab by remember { mutableStateOf(0) } // 0: Hajj, 1: Umrah, 2: Checklist
    val checklist = HajjData.checklistItems

    // Checklist check states
    val checkStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            checklist.forEachIndexed { idx, _ ->
                put(idx, prefs.getBoolean("hajj_check_$idx", false))
            }
        }
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Pilgrimage Guide",
            onNavigateBack = onNavigateBack
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(if (activeTab == 2) SalamSpacing.elementGap else SalamSpacing.cardGap),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

                // Tab Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SalamSpacing.cardGap),
                    horizontalArrangement = Arrangement.spacedBy(SalamSpacing.elementGap)
                ) {
                    listOf("Hajj Guide", "Umrah Guide", "Checklist").forEachIndexed { index, name ->
                        val isSel = activeTab == index
                        val tabShape = if (isSel) SalamShapes.expressiveCorner1 else SalamShapes.cardSmall
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(tabShape)
                                .background(if (isSel) palette.primary else palette.surface.copy(alpha = 0.85f))
                                .salamClickable { activeTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) palette.background else palette.textPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (activeTab) {
                0 -> {
                    itemsIndexed(HajjData.hajjSteps, key = { _, step -> "hajj-${step.id}" }) { idx, step ->
                        PilgrimageStepRow(
                            step = step,
                            index = idx + 1,
                            clipboard = clipboard,
                            haptic = haptic,
                            palette = palette
                        )
                    }
                }
                1 -> {
                    itemsIndexed(HajjData.umrahSteps, key = { _, step -> "umrah-${step.id}" }) { idx, step ->
                        PilgrimageStepRow(
                            step = step,
                            index = idx + 1,
                            clipboard = clipboard,
                            haptic = haptic,
                            palette = palette
                        )
                    }
                }
                2 -> {
                    itemsIndexed(checklist, key = { _, item -> item }) { idx, item ->
                        val isChecked = checkStates[idx] ?: false
                        val checkShape = if (idx % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
                        SalamCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .staggeredEntrance(idx),
                            elevation = 2,
                            isActive = isChecked,
                            shape = checkShape,
                            onClick = {
                                val next = !isChecked
                                checkStates[idx] = next
                                prefs.edit().putBoolean("hajj_check_$idx", next).apply()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SalamSpacing.cardPaddingInner),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isChecked) palette.textSecondary else palette.textPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) palette.primary else palette.surfaceVariant)
                                        .border(1.dp, if (isChecked) palette.primary else palette.outline.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(Icons.Default.Check, null, tint = palette.background, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
            }
        }
    }
}

@Composable
fun PilgrimageStepRow(
    step: HajjStep,
    index: Int,
    clipboard: androidx.compose.ui.platform.ClipboardManager,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    palette: com.yehia.prayertimes.ui.theme.ThemePalette,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val stepShape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
    SalamCard(
        modifier = modifier.fillMaxWidth(),
        elevation = if (isExpanded) 3 else 2,
        isActive = isExpanded,
        shape = stepShape,
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SalamSpacing.cardPaddingInner),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(palette.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.primary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    )
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.labelSmall.copy(color = palette.textSecondary)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = palette.textSecondary
                )
            }

            // Expanded content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = SalamSpacing.cardPaddingInner, end = SalamSpacing.cardPaddingInner, bottom = SalamSpacing.cardPaddingInner)
                ) {
                    HorizontalDivider(color = palette.outline.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = SalamSpacing.elementGap))
                    Text(
                        text = step.detail,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = palette.textSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    if (step.duaArabic.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(SalamSpacing.cardGap))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.surfaceVariant.copy(alpha = 0.4f), SalamShapes.cardSmall)
                                .border(1.dp, palette.outline.copy(alpha = 0.25f), SalamShapes.cardSmall)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Supplication",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = palette.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            val copyStr = "\"${step.duaArabic}\"\n\nTransliteration: ${step.duaTranslation}"
                                            clipboard.setText(AnnotatedString(copyStr))
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, null, tint = palette.textSecondary, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = step.duaArabic,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    style = LocalTextStyle.current.copy(
                                        textDirection = TextDirection.Rtl,
                                        lineHeight = 28.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step.duaTranslation,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontStyle = FontStyle.Italic,
                                        color = palette.primary,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
