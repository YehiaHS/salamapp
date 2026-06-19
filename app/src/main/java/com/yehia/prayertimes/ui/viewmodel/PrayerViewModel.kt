package com.yehia.prayertimes.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Madhab
import com.yehia.prayertimes.data.PrayerCalculationResult
import com.yehia.prayertimes.data.PrayerRepository
import com.yehia.prayertimes.utils.LocationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yehia.prayertimes.utils.NotificationHelper

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PrayerRepository()

    // Location Coordinates
    private val _latitude = MutableStateFlow(NotificationHelper.getSavedLatitude(application))
    val latitude: StateFlow<Double> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(NotificationHelper.getSavedLongitude(application))
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    // Settings Configuration
    private val _calculationMethod = MutableStateFlow(CalculationMethod.EGYPTIAN)
    val calculationMethod: StateFlow<CalculationMethod> = _calculationMethod.asStateFlow()

    private val _madhab = MutableStateFlow(Madhab.SHAFI)
    val madhab: StateFlow<Madhab> = _madhab.asStateFlow()

    // Prayer Calculation Result Flow
    private val _uiState = MutableStateFlow<PrayerCalculationResult?>(null)
    val uiState: StateFlow<PrayerCalculationResult?> = _uiState.asStateFlow()

    // Countdown UI Formatting
    private val _countdownStr = MutableStateFlow("00:00:00")
    val countdownStr: StateFlow<String> = _countdownStr.asStateFlow()

    // Progress towards the next prayer (value between 0.0f and 1.0f)
    private val _nextPrayerProgress = MutableStateFlow(0.0f)
    val nextPrayerProgress: StateFlow<Float> = _nextPrayerProgress.asStateFlow()

    // Native Magnetometer Azimuth (relative to magnetic North)
    private val _deviceAzimuth = MutableStateFlow(0.0f)
    val deviceAzimuth: StateFlow<Float> = _deviceAzimuth.asStateFlow()

    private var timerJob: Job? = null

    init {
        recalculate()
        startTimer()
    }

    fun setLocation(lat: Double, lng: Double) {
        _latitude.value = lat
        _longitude.value = lng
        recalculate()
    }

    fun updateCalculationMethod(method: CalculationMethod) {
        _calculationMethod.value = method
        recalculate()
    }

    fun updateMadhab(madhab: Madhab) {
        _madhab.value = madhab
        recalculate()
    }

    fun updateDeviceAzimuth(azimuth: Float) {
        // Apply low pass filter or simply direct update
        _deviceAzimuth.value = azimuth
    }

    fun recalculate() {
        _uiState.value = repository.calculatePrayerTimes(
            latitude = _latitude.value,
            longitude = _longitude.value,
            method = _calculationMethod.value,
            madhab = _madhab.value
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val currentResult = _uiState.value
                if (currentResult != null && currentResult.nextPrayer != null) {
                    val nowMs = System.currentTimeMillis()
                    val nextTimeMs = currentResult.nextPrayer.time.time
                    val remainingMs = (nextTimeMs - nowMs).coerceAtLeast(0L)

                    _countdownStr.value = formatMsToTimer(remainingMs)

                    val currentStart = currentResult.currentPrayer?.time?.time ?: 0L
                    val totalDuration = (nextTimeMs - currentStart).coerceAtLeast(1L)
                    val elapsed = (nowMs - currentStart).coerceAtLeast(0L)
                    val progress = (elapsed.toFloat() / totalDuration.toFloat()).coerceIn(0.0f, 1.0f)
                    
                    _nextPrayerProgress.value = progress

                    if (remainingMs <= 0L) {
                        recalculate()
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun formatMsToTimer(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
