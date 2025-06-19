package com.example.mapdemo.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.mapdemo.data.LocationData
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
        .setMinUpdateDistanceMeters(1f)
        .setMinUpdateIntervalMillis(500)
        .setMaxUpdateDelayMillis(2000)
        .build()
    
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<LocationData> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val locationData = LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        bearing = location.bearing,
                        speed = location.speed
                    )
                    trySend(locationData)
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnSuccessListener {
                // Location updates started successfully
            }.addOnFailureListener { exception ->
                close(exception)
            }
        } catch (e: SecurityException) {
            close(e)
        }
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                LocationData(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracy = it.accuracy,
                    bearing = it.bearing,
                    speed = it.speed
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun calculateDistance(from: LocationData, to: LocationData): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0]
    }
    
    fun calculateBearing(from: LocationData, to: LocationData): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lat2 = Math.toRadians(to.latitude)
        val deltaLon = Math.toRadians(to.longitude - from.longitude)
        
        val y = Math.sin(deltaLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon)
        
        val bearing = Math.toDegrees(Math.atan2(y, x))
        return ((bearing + 360) % 360).toFloat()
    }
    
    fun isGpsSignalGood(location: LocationData): Boolean {
        return location.accuracy <= 10f // 10 meters accuracy threshold
    }
} 