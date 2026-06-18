package com.yehia.prayertimes.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Helper to fetch coordinates from Android FusedLocationProvider.
 * Falls back to default coordinates of Cairo, Egypt (29.976486, 31.131302) if location is unavailable.
 */
object LocationHelper {

    val DEFAULT_LATITUDE = 29.976486
    val DEFAULT_LONGITUDE = 31.131302

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        onLocationFetched: (Double, Double) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        try {
            // Check if we can get last known location quickly
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onLocationFetched(location.latitude, location.longitude)
                    } else {
                        // Request active high accuracy location update
                        val cts = CancellationTokenSource()
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cts.token
                        ).addOnSuccessListener { activeLocation: Location? ->
                            if (activeLocation != null) {
                                onLocationFetched(activeLocation.latitude, activeLocation.longitude)
                            } else {
                                // Fallback to Cairo
                                onLocationFetched(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                            }
                        }.addOnFailureListener {
                            onLocationFetched(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                        }
                    }
                }
                .addOnFailureListener {
                    onLocationFetched(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                }
        } catch (e: SecurityException) {
            // Permissions missing, fallback to Cairo
            onLocationFetched(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        }
    }
}
