package com.yehia.prayertimes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yehia.prayertimes.ui.theme.SalamCard
import com.yehia.prayertimes.ui.theme.SalamTopBar
import com.yehia.prayertimes.ui.theme.SalamScreenScaffold
import com.yehia.prayertimes.ui.theme.SalamSpacing
import com.yehia.prayertimes.ui.theme.SalamShapes
import com.yehia.prayertimes.ui.theme.ThemeManager
import com.yehia.prayertimes.ui.theme.staggeredEntrance
import com.yehia.prayertimes.ui.theme.salamClickable
import com.yehia.prayertimes.utils.HijriCalendar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HijriCalendarScreen(
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val todayHijri = remember { HijriCalendar.getTodayHijri() }

    val monthNameAr = remember(todayHijri) { HijriCalendar.getHijriMonthName(todayHijri.month) }
    val monthNameEn = remember(todayHijri) { HijriCalendar.getHijriMonthNameEnglish(todayHijri.month) }

    val todayGregorianStr = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)
        sdf.format(Calendar.getInstance().time)
    }

    // List of all events
    val allEvents = remember {
        listOf(
            IslamicEvent(1, 1, "Islamic New Year", "1 Muharram", "Marks the beginning of the Hijri year."),
            IslamicEvent(1, 10, "Day of Ashura", "10 Muharram", "Day of fasting and commemoration of historical events."),
            IslamicEvent(3, 12, "Mawlid an-Nabi", "12 Rabi al-Awwal", "Observance of the Prophet Muhammad's birthday."),
            IslamicEvent(7, 27, "Isra and Mi'raj", "27 Rajab", "The miraculous Night Journey and Ascension of the Prophet."),
            IslamicEvent(8, 15, "Laylat al-Bara'ah", "15 Sha'ban", "Night of forgiveness and salvation."),
            IslamicEvent(9, 1, "Start of Ramadan", "1 Ramadan", "Beginning of the month of mercy, fasting, and spiritual reflection."),
            IslamicEvent(9, 27, "Laylat al-Qadr (Approx)", "27 Ramadan", "The Night of Power, better than a thousand months."),
            IslamicEvent(10, 1, "Eid al-Fitr", "1 Shawwal", "Festival celebrating the completion of Ramadan fasting."),
            IslamicEvent(12, 9, "Day of Arafah", "9 Dhul-Hijjah", "The peak of the Hajj pilgrimage, best day for fasting and supplication."),
            IslamicEvent(12, 10, "Eid al-Adha", "10 Dhul-Hijjah", "Festival of sacrifice commemorating Prophet Ibrahim's obedience.")
        )
    }

    // Filter events occurring in this specific month
    val monthlyEvents = remember(todayHijri) {
        allEvents.filter { it.hijriMonth == todayHijri.month }
    }

    SalamScreenScaffold {
        SalamTopBar(
            title = "Hijri Calendar",
            onNavigateBack = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SalamSpacing.cardGap)
        ) {
            // Calendar Hero Header Card
            item {
                Spacer(modifier = Modifier.height(SalamSpacing.elementGap))
                SalamCard(
                    modifier = Modifier.fillMaxWidth().staggeredEntrance(0),
                    elevation = 2,
                    shape = SalamShapes.squircle
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SalamSpacing.cardPaddingInnerLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(palette.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))

                        // Hijri Date Text
                        Text(
                            text = todayHijri.day.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = palette.primary
                            )
                        )

                        Text(
                            text = "$monthNameEn ($monthNameAr)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        )

                        Text(
                            text = "Year ${todayHijri.year} AH",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = palette.textSecondary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))
                        HorizontalDivider(color = palette.outline.copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(SalamSpacing.cardPaddingInner))

                        // Gregorian equivalence
                        Text(
                            text = "Gregorian Date",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.textMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todayGregorianStr,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = palette.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Events Title
            item {
                Text(
                    text = "Islamic Events this Month",
                    color = palette.primary,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = SalamSpacing.elementGap)
                )
            }

            // Monthly events list
            if (monthlyEvents.isEmpty()) {
                item {
                    SalamCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 1
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SalamSpacing.cardPaddingInnerLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No major events recorded in $monthNameEn.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = palette.textSecondary
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(monthlyEvents, key = { _, it -> it.title }) { index, event ->
                    Box(modifier = Modifier.staggeredEntrance(index + 1)) {
                        EventItemCard(event = event, index = index)
                    }
                }
            }

            // Spacing
            item {
                Spacer(modifier = Modifier.height(SalamSpacing.sectionGap))
            }
        }
    }
}

@Composable
fun EventItemCard(event: IslamicEvent, index: Int) {
    val palette by ThemeManager.currentPalette.collectAsState()

    val shape = if (index % 2 == 0) SalamShapes.expressiveCorner1 else SalamShapes.expressiveCorner2
    SalamCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SalamSpacing.cardPaddingInner),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(palette.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(SalamSpacing.cardPaddingInner))
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                )
                Text(
                    text = event.dateStr,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = palette.primary
                    )
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = palette.textSecondary,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

data class IslamicEvent(
    val hijriMonth: Int,
    val hijriDay: Int,
    val title: String,
    val dateStr: String,
    val description: String
)
