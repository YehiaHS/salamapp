package com.yehia.prayertimes.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val context = LocalContext.current

    // Calculator inputs
    var cashInput by remember { mutableStateOf("") }
    var goldInput by remember { mutableStateOf("") }
    var assetsInput by remember { mutableStateOf("") }
    var debtsInput by remember { mutableStateOf("") }

    // Nisab value (Mock set to $6000 USD)
    val nisabLimit = 6000.0

    // Calculations
    val cash = cashInput.toDoubleOrNull() ?: 0.0
    val gold = goldInput.toDoubleOrNull() ?: 0.0
    val assets = assetsInput.toDoubleOrNull() ?: 0.0
    val debts = debtsInput.toDoubleOrNull() ?: 0.0

    val netAssets = (cash + gold + assets - debts).coerceAtLeast(0.0)
    val isEligible = netAssets >= nisabLimit
    val zakatDue = if (isEligible) netAssets * 0.025 else 0.0

    var showDonationDialog by remember { mutableStateOf(false) }
    var donationCampaignName by remember { mutableStateOf("") }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Zakat Calculator",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
        ) {
            Spacer(modifier = Modifier.height(SalamSpacing.elementGap))

            // Results Card (Hero Status)
            SalamCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(0),
                elevation = 3,
                shape = SalamShapes.squircle,
                isActive = isEligible
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SalamSpacing.cardPaddingInnerLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Zakat Due Amount",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = palette.textSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                    Text(
                        text = String.format(Locale.US, "$%.2f", zakatDue),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isEligible) palette.primary else palette.textMuted
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))
                    HorizontalDivider(color = palette.outline.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Net Assets",
                                style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted)
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", netAssets),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Current Nisab Threshold",
                                style = MaterialTheme.typography.labelSmall.copy(color = palette.textMuted)
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", nisabLimit),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // ── Inputs Card ─────────────────────────────────────────
            SalamSectionHeader(
                title = "Calculate Assets",
                modifier = Modifier.staggeredEntrance(1)
            )

            SalamCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntrance(2),
                elevation = 2,
                shape = SalamShapes.expressiveCorner1
            ) {
                Column(
                    modifier = Modifier.padding(SalamSpacing.cardPaddingInner),
                    verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
                ) {
                    ZakatInputField(value = cashInput, onValueChange = { cashInput = it }, label = "Cash in Hand & Bank Accounts ($)", index = 3)
                    ZakatInputField(value = goldInput, onValueChange = { goldInput = it }, label = "Gold & Silver Market Value ($)", index = 4)
                    ZakatInputField(value = assetsInput, onValueChange = { assetsInput = it }, label = "Investment / Business Assets ($)", index = 5)
                    ZakatInputField(value = debtsInput, onValueChange = { debtsInput = it }, label = "Liabilities & Immediate Debts ($)", index = 6)
                }
            }

            // ── Charity Donations ───────────────────────────────────
            SalamSectionHeader(
                title = "Sadaqah & Seasonal Charity",
                modifier = Modifier.staggeredEntrance(7)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
            ) {
                // Sadaqah Card
                SalamCard(
                    modifier = Modifier
                        .weight(1f)
                        .staggeredEntrance(8),
                    elevation = 2,
                    shape = SalamShapes.expressiveCorner1,
                    onClick = {
                        donationCampaignName = "General Sadaqah"
                        showDonationDialog = true
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SalamSpacing.cardPaddingInner),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SalamIconBadge {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(SalamSpacing.iconSize)
                            )
                        }
                        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                        Text(
                            text = "General Sadaqah",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Give voluntary charity",
                            style = MaterialTheme.typography.labelSmall.copy(color = palette.textSecondary),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Qurban Card
                SalamCard(
                    modifier = Modifier
                        .weight(1f)
                        .staggeredEntrance(9),
                    elevation = 2,
                    shape = SalamShapes.expressiveCorner2,
                    onClick = {
                        donationCampaignName = "Seasonal Qurban Campaign"
                        showDonationDialog = true
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SalamSpacing.cardPaddingInner),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SalamIconBadge {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(SalamSpacing.iconSize)
                            )
                        }
                        Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                        Text(
                            text = "Seasonal Qurban",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Eid-al-Adha offerings",
                            style = MaterialTheme.typography.labelSmall.copy(color = palette.textSecondary),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
        }
    }

    // Donation Confirmation Dialog
    if (showDonationDialog) {
        AlertDialog(
            onDismissRequest = { showDonationDialog = false },
            title = {
                Text(
                    text = "Donate Sadaqah",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "Thank you for supporting the $donationCampaignName campaign. This voluntary mock donation has been logged in your account receipt diary.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = palette.textSecondary)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDonationDialog = false
                    Toast.makeText(context, "Donation receipt printed in debug logs!", Toast.LENGTH_SHORT).show()
                }) {
                    Text(
                        text = "Conclude",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = palette.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            containerColor = palette.surface,
            shape = SalamShapes.squircle
        )
    }
}

@Composable
fun ZakatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    index: Int
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            val cleaned = input.filter { char -> char.isDigit() || char == '.' }
            val firstDotIdx = cleaned.indexOf('.')
            val robustCleaned = if (firstDotIdx != -1) {
                cleaned.substring(0, firstDotIdx + 1) + cleaned.substring(firstDotIdx + 1).replace(".", "")
            } else {
                cleaned
            }
            onValueChange(robustCleaned)
        },
        modifier = Modifier
            .fillMaxWidth()
            .staggeredEntrance(index),

        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = SalamShapes.cardSmall,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.surfaceVariant,
            focusedLabelColor = palette.primary,
            cursorColor = palette.primary,
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary
        )
    )
}
