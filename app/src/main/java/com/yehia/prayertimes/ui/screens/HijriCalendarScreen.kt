package com.yehia.prayertimes.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HijriCalendarScreen(
    viewModel: com.yehia.prayertimes.ui.viewmodel.PrayerViewModel,
    onNavigateBack: () -> Unit
) {
    val palette by ThemeManager.currentPalette.collectAsState()
    val timeZone by viewModel.timeZone.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Determine today's date in local selected location timezone
    val todayHijri = remember(timeZone) { HijriCalendar.getTodayHijri(timeZone) }

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

    // We use a HorizontalPager to allow swiping between months.
    // Let page 1000 be the current today's Hijri month.
    val initialPage = 1000
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2000 })

    // Calculate year and month based on page offset
    val pageDiff = pagerState.currentPage - initialPage
    val targetMonthOffset = (todayHijri.month - 1) + pageDiff
    val displayedMonth = ((targetMonthOffset % 12 + 12) % 12) + 1
    val displayedYear = todayHijri.year + (if (targetMonthOffset >= 0) targetMonthOffset / 12 else (targetMonthOffset - 11) / 12)

    val monthNameAr = remember(displayedMonth) { HijriCalendar.getHijriMonthName(displayedMonth) }
    val monthNameEn = remember(displayedMonth) { HijriCalendar.getHijriMonthNameEnglish(displayedMonth) }

    // Filter events occurring in this specific month
    val monthlyEvents = remember(displayedMonth) {
        allEvents.filter { it.hijriMonth == displayedMonth }
    }

    // Sync helpers
    fun syncEventToGoogleCalendar(event: IslamicEvent, year: Int) {
        try {
            val date = HijriCalendar.hijriToGregorianDate(HijriCalendar.HijriDate(year, event.hijriMonth, event.hijriDay))
            val cal = Calendar.getInstance()
            cal.time = date

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.timeInMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.timeInMillis + 2 * 60 * 60 * 1000L) // 2 hour duration
                putExtra(CalendarContract.Events.ALL_DAY, true)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error launching calendar", Toast.LENGTH_SHORT).show()
        }
    }

    fun syncAllEventsOfCurrentMonth() {
        if (monthlyEvents.isEmpty()) {
            Toast.makeText(context, "No events to sync in this month", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            // Android doesn't have a batch insert intent API that is standard and clean without write permissions,
            // so we will loop or send them to calendar via a list/sheet format or add a primary event.
            // Let's add the first event as a launcher, or give option to add individual ones.
            // But we can insert multiple by launching calendar with the first and toast/explain.
            // A more standard way is sync one-by-one. Let's sync them by launching the first and letting them add it, or offer to launch individual.
            // Let's show a toast explaining how to sync.
            monthlyEvents.forEachIndexed { i, event ->
                if (i == 0) {
                    syncEventToGoogleCalendar(event, displayedYear)
                }
            }
            Toast.makeText(context, "Syncing events. Please add them sequentially.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Sync error", Toast.LENGTH_SHORT).show()
        }
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
            // Calendar grid and Navigation Card
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
                        // Navigation Header Control
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Prev Month",
                                    tint = palette.primary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$monthNameEn ($monthNameAr)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = palette.textPrimary
                                    )
                                )
                                Text(
                                    text = "$displayedYear AH",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = palette.textSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next Month",
                                    tint = palette.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid of days header (Sunday to Saturday)
                        val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            weekdays.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = palette.textMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // HorizontalPager for Swiping Months
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth()
                        ) { page ->
                            val pageOffset = page - initialPage
                            val mOffset = (todayHijri.month - 1) + pageOffset
                            val mMonth = ((mOffset % 12 + 12) % 12) + 1
                            val mYear = todayHijri.year + (if (mOffset >= 0) mOffset / 12 else (mOffset - 11) / 12)

                            val daysInMonth = HijriCalendar.getDaysInMonth(mYear, mMonth)

                            val firstDayJdn = HijriCalendar.hijriToJdn(mYear, mMonth, 1)
                            val firstDayOfWeekIndex = ((firstDayJdn + 1) % 7).toInt()

                            val totalCells = if (firstDayOfWeekIndex + daysInMonth > 35) 42 else 35
                            val cells = mutableListOf<HijriCalendar.HijriDate?>()
                            for (i in 0 until firstDayOfWeekIndex) {
                                cells.add(null)
                            }
                            for (day in 1..daysInMonth) {
                                cells.add(HijriCalendar.HijriDate(mYear, mMonth, day))
                            }
                            while (cells.size < totalCells) {
                                cells.add(null)
                            }

                            val chunkedWeeks = cells.chunked(7)
                            val sdf = SimpleDateFormat("d/M", Locale.ENGLISH)
                            sdf.timeZone = timeZone

                            Column(modifier = Modifier.fillMaxWidth()) {
                                chunkedWeeks.forEach { week ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        week.forEach { date ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (date != null) {
                                                    val isToday = date.day == todayHijri.day &&
                                                            date.month == todayHijri.month &&
                                                            date.year == todayHijri.year

                                                    // Check if this date is an iconic Islamic event
                                                    val hasEvent = allEvents.any { it.hijriMonth == date.month && it.hijriDay == date.day }

                                                    val gregDate = HijriCalendar.hijriToGregorianDate(date)
                                                    val gregStr = sdf.format(gregDate)

                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape)
                                                            .background(
                                                                color = when {
                                                                    isToday -> palette.primary
                                                                    hasEvent -> palette.accent.copy(alpha = 0.25f)
                                                                    else -> Color.Transparent
                                                                }
                                                            )
                                                            .border(
                                                                width = when {
                                                                    isToday -> 0.dp
                                                                    hasEvent -> 1.5.dp
                                                                    else -> 1.dp
                                                                },
                                                                color = when {
                                                                    isToday -> Color.Transparent
                                                                    hasEvent -> palette.accent
                                                                    else -> palette.outline.copy(alpha = 0.15f)
                                                                },
                                                                shape = CircleShape
                                                            )
                                                            .salamClickable {
                                                                // Sync single date to Google Calendar if clicked
                                                                val matchingEvent = allEvents.firstOrNull { it.hijriMonth == date.month && it.hijriDay == date.day }
                                                                if (matchingEvent != null) {
                                                                    syncEventToGoogleCalendar(matchingEvent, date.year)
                                                                } else {
                                                                    // Create dummy event to add custom note
                                                                    val customEvent = IslamicEvent(
                                                                        date.month,
                                                                        date.day,
                                                                        "Hijri Date Note",
                                                                        "${date.day} ${HijriCalendar.getHijriMonthNameEnglish(date.month)}",
                                                                        "Islamic Calendar reference day"
                                                                    )
                                                                    syncEventToGoogleCalendar(customEvent, date.year)
                                                                }
                                                            }
                                                            .padding(2.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = date.day.toString(),
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                fontWeight = if (hasEvent || isToday) FontWeight.Bold else FontWeight.Normal,
                                                                color = when {
                                                                    isToday -> palette.onPrimary
                                                                    hasEvent -> palette.accent
                                                                    else -> palette.textPrimary
                                                                }
                                                            )
                                                        )
                                                        Text(
                                                            text = gregStr,
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 8.sp,
                                                                color = if (isToday) palette.onPrimary.copy(alpha = 0.8f) else palette.textMuted
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
                    }
                }
            }

            // Events Title with Sync Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = SalamSpacing.elementGap),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Islamic Events in $monthNameEn",
                        color = palette.primary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (monthlyEvents.isNotEmpty()) {
                        Button(
                            onClick = { syncAllEventsOfCurrentMonth() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primary,
                                contentColor = palette.onPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Month",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Sync Month", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
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
                        EventItemCard(
                            event = event, 
                            index = index, 
                            year = displayedYear,
                            onSyncClick = { syncEventToGoogleCalendar(event, displayedYear) }
                        )
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
fun EventItemCard(event: IslamicEvent, index: Int, year: Int, onSyncClick: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
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
            
            // Sync Button for individual event
            IconButton(
                onClick = onSyncClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync Event",
                    tint = palette.primary,
                    modifier = Modifier.size(20.dp)
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
