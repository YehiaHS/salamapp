package com.yehia.prayertimes.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val systemPalette = ThemeManager.currentPalette.collectAsState().value

    // Load initial values from ThemeManager's custom palette
    val savedCustomPalette = ThemeManager.customPalette.collectAsState().value

    var isLight by remember { mutableStateOf(savedCustomPalette.isLight) }
    var primaryR by remember { mutableStateOf(savedCustomPalette.primary.red) }
    var primaryG by remember { mutableStateOf(savedCustomPalette.primary.green) }
    var primaryB by remember { mutableStateOf(savedCustomPalette.primary.blue) }

    var accentR by remember { mutableStateOf(savedCustomPalette.accent.red) }
    var accentG by remember { mutableStateOf(savedCustomPalette.accent.green) }
    var accentB by remember { mutableStateOf(savedCustomPalette.accent.blue) }

    var bgR by remember { mutableStateOf(savedCustomPalette.background.red) }
    var bgG by remember { mutableStateOf(savedCustomPalette.background.green) }
    var bgB by remember { mutableStateOf(savedCustomPalette.background.blue) }

    // Enforce safety constraints on background color based on light/dark mode
    val adjustedBg = remember(bgR, bgG, bgB, isLight) {
        if (isLight) {
            Color(
                red = bgR.coerceIn(0.75f, 1.0f),
                green = bgG.coerceIn(0.75f, 1.0f),
                blue = bgB.coerceIn(0.75f, 1.0f)
            )
        } else {
            Color(
                red = bgR.coerceIn(0.0f, 0.25f),
                green = bgG.coerceIn(0.0f, 0.25f),
                blue = bgB.coerceIn(0.0f, 0.25f)
            )
        }
    }

    val primaryColor = Color(primaryR, primaryG, primaryB)
    val accentColor = Color(accentR, accentG, accentB)

    // Compute dynamic custom palette for live preview
    val previewPalette = remember(primaryColor, accentColor, adjustedBg, isLight) {
        generateCustomPalette(primaryColor, accentColor, adjustedBg, isLight)
    }

    // Helper to apply preset
    fun applyPreset(pColor: Color, aColor: Color, bColor: Color, lightMode: Boolean) {
        primaryR = pColor.red
        primaryG = pColor.green
        primaryB = pColor.blue
        accentR = aColor.red
        accentG = aColor.green
        accentB = aColor.blue
        bgR = bColor.red
        bgG = bColor.green
        bgB = bColor.blue
        isLight = lightMode
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Theme Builder",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

            // ── LIVE PREVIEW SECTION ─────────────────────────────────
            SalamSectionHeader(title = "Live Preview")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.sectionGap),
                shape = SalamShapes.cardLarge,
                colors = CardDefaults.cardColors(containerColor = previewPalette.background),
                border = BorderStroke(1.5.dp, previewPalette.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SalamSpacing.cardPaddingInnerLarge)
                ) {
                    // Top Bar Mockup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cairo, Egypt",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = previewPalette.textPrimary
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(previewPalette.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(previewPalette.primary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

                    // Next Prayer Hero Card Mockup
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = SalamShapes.cardMedium,
                        colors = CardDefaults.cardColors(containerColor = previewPalette.surface),
                        border = BorderStroke(1.dp, previewPalette.primary.copy(alpha = 0.4f))
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
                                    text = "Fajr",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = previewPalette.textPrimary
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(SalamShapes.cardSmall)
                                        .background(previewPalette.accent.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Next",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = previewPalette.accent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "04:15:32",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = previewPalette.primary
                                )
                            )

                            Text(
                                text = "Remaining",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = previewPalette.textSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(SalamSpacing.cardGap))

                    // Mini List item mockup
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SalamShapes.cardMedium)
                            .background(previewPalette.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = SalamSpacing.cardPaddingInner, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Dhikr Counter",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = previewPalette.textPrimary
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(previewPalette.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                color = previewPalette.background,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // ── PRESETS SEED SECTION ──────────────────────────────────
            SalamSectionHeader(title = "Starting Presets")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.sectionGap),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Preset 1: Neon Teal
                Button(
                    onClick = { applyPreset(Color(0xFF00E5A3), Color(0xFFFFB300), Color(0xFF060A12), false) },
                    colors = ButtonDefaults.buttonColors(containerColor = systemPalette.surfaceVariant),
                    border = BorderStroke(1.dp, systemPalette.outline.copy(alpha = 0.3f)),
                    shape = SalamShapes.cardSmall,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E5A3))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Teal", fontSize = 11.sp, color = systemPalette.textPrimary)
                    }
                }

                // Preset 2: Sunset Gold
                Button(
                    onClick = { applyPreset(Color(0xFFFFD54F), Color(0xFFFF5722), Color(0xFF120E08), false) },
                    colors = ButtonDefaults.buttonColors(containerColor = systemPalette.surfaceVariant),
                    border = BorderStroke(1.dp, systemPalette.outline.copy(alpha = 0.3f)),
                    shape = SalamShapes.cardSmall,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD54F))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sunset", fontSize = 11.sp, color = systemPalette.textPrimary)
                    }
                }

                // Preset 3: Lavender Light
                Button(
                    onClick = { applyPreset(Color(0xFF7C4DFF), Color(0xFFFF5252), Color(0xFFF9F7FC), true) },
                    colors = ButtonDefaults.buttonColors(containerColor = systemPalette.surfaceVariant),
                    border = BorderStroke(1.dp, systemPalette.outline.copy(alpha = 0.3f)),
                    shape = SalamShapes.cardSmall,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7C4DFF))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Lavender", fontSize = 11.sp, color = systemPalette.textPrimary)
                    }
                }

                // Preset 4: Desert Rose
                Button(
                    onClick = { applyPreset(Color(0xFFE8A0BF), Color(0xFFD4A574), Color(0xFF1E1414), false) },
                    colors = ButtonDefaults.buttonColors(containerColor = systemPalette.surfaceVariant),
                    border = BorderStroke(1.dp, systemPalette.outline.copy(alpha = 0.3f)),
                    shape = SalamShapes.cardSmall,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8A0BF))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Rose", fontSize = 11.sp, color = systemPalette.textPrimary)
                    }
                }
            }

            // ── CONTROLS SECTION ─────────────────────────────────────
            SalamSectionHeader(title = "Customization Sliders")

            SalamCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.sectionGap),
                elevation = 2
            ) {
                Column(modifier = Modifier.padding(SalamSpacing.cardPaddingInner)) {
                    // Light / Dark Mode Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Base Mode",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = systemPalette.textPrimary
                                )
                            )
                            Text(
                                text = if (isLight) "Light theme base" else "Dark theme base",
                                style = MaterialTheme.typography.bodySmall.copy(color = systemPalette.textSecondary)
                            )
                        }
                        Switch(
                            checked = isLight,
                            onCheckedChange = { isLight = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = systemPalette.primary,
                                checkedTrackColor = systemPalette.primary.copy(alpha = 0.3f),
                                uncheckedThumbColor = systemPalette.textMuted,
                                uncheckedTrackColor = systemPalette.surfaceVariant
                            )
                        )
                    }

                    HorizontalDivider(
                        color = systemPalette.outline.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // 1. Primary RGB Slider Group
                    ColorSliders(
                        title = "Primary Theme Accent",
                        color = primaryColor,
                        onColorChanged = { color ->
                            primaryR = color.red
                            primaryG = color.green
                            primaryB = color.blue
                        },
                        palette = systemPalette
                    )

                    HorizontalDivider(
                        color = systemPalette.outline.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // 2. Accent RGB Slider Group
                    ColorSliders(
                        title = "Secondary Accent Highlight",
                        color = accentColor,
                        onColorChanged = { color ->
                            accentR = color.red
                            accentG = color.green
                            accentB = color.blue
                        },
                        palette = systemPalette
                    )

                    HorizontalDivider(
                        color = systemPalette.outline.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // 3. Background RGB Slider Group (Safe capped background)
                    ColorSliders(
                        title = "Background Tint",
                        color = adjustedBg,
                        onColorChanged = { color ->
                            bgR = color.red
                            bgG = color.green
                            bgB = color.blue
                        },
                        palette = systemPalette
                    )
                }
            }

            // ── ACTION BUTTONS ────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SalamSpacing.sectionGap),
                horizontalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
            ) {
                // Cancel
                OutlinedButton(
                    onClick = onNavigateBack,
                    shape = SalamShapes.cardMedium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = systemPalette.textPrimary),
                    border = BorderStroke(1.dp, systemPalette.outline.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }

                // Reset
                OutlinedButton(
                    onClick = {
                        // Reset to ThemeManager default custom palette values
                        applyPreset(Color(0xFFD0BCFF), Color(0xFFEFB8C8), Color(0xFF0F0B1E), false)
                    },
                    shape = SalamShapes.cardMedium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = systemPalette.textSecondary),
                    border = BorderStroke(1.dp, systemPalette.outline.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold)
                }

                // Save & Apply
                Button(
                    onClick = {
                        // Save custom palette to SharedPreferences
                        ThemeManager.saveCustomPalette(
                            context,
                            primaryColor,
                            accentColor,
                            adjustedBg,
                            isLight
                        )
                        // Select custom theme
                        ThemeManager.setTheme(context, AppTheme.CUSTOM)
                        ThemeManager.saveTheme(context, AppTheme.CUSTOM)
                        // Navigate back
                        onNavigateBack()
                    },
                    shape = SalamShapes.cardMedium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = systemPalette.primary,
                        contentColor = systemPalette.background
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                ) {
                    Text("Save & Apply", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ColorSliders(
    title: String,
    color: Color,
    onColorChanged: (Color) -> Unit,
    palette: ThemePalette
) {
    var r by remember(color) { mutableStateOf(color.red) }
    var g by remember(color) { mutableStateOf(color.green) }
    var b by remember(color) { mutableStateOf(color.blue) }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            )
            // Color circle preview
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(r, g, b))
                    .border(1.dp, palette.outline.copy(alpha = 0.4f), CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

        Column(modifier = Modifier.fillMaxWidth()) {
            // Red Slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "R",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    ),
                    modifier = Modifier.width(20.dp)
                )
                Slider(
                    value = r,
                    onValueChange = {
                        r = it
                        onColorChanged(Color(r, g, b))
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Red,
                        activeTrackColor = Color.Red.copy(alpha = 0.5f),
                        inactiveTrackColor = palette.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (r * 255).toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Green Slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "G",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.width(20.dp)
                )
                Slider(
                    value = g,
                    onValueChange = {
                        g = it
                        onColorChanged(Color(r, g, b))
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF2E7D32),
                        activeTrackColor = Color(0xFF2E7D32).copy(alpha = 0.5f),
                        inactiveTrackColor = palette.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (g * 255).toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Blue Slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "B",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue
                    ),
                    modifier = Modifier.width(20.dp)
                )
                Slider(
                    value = b,
                    onValueChange = {
                        b = it
                        onColorChanged(Color(r, g, b))
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Blue,
                        activeTrackColor = Color.Blue.copy(alpha = 0.5f),
                        inactiveTrackColor = palette.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (b * 255).toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
