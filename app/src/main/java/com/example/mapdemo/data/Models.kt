package com.example.mapdemo.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val bearing: Float = 0f,
    val speed: Float = 0f
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}

data class TripData(
    val startLocation: LocationData? = null,
    val destination: LocationData? = null,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val totalDistance: Float = 0f,
    val pathPoints: List<LatLng> = emptyList(),
    val isCompleted: Boolean = false
)

data class NavigationState(
    val currentLocation: LocationData? = null,
    val destination: LocationData? = null,
    val isNavigating: Boolean = false,
    val tripData: TripData = TripData(),
    val gpsSignalLost: Boolean = false,
    val pathPoints: List<LatLng> = emptyList(),
    val estimatedTimeToDestination: Long = 0L,
    val distanceToDestination: Float = 0f
)

sealed class NavigationEvent {
    object StartNavigation : NavigationEvent()
    object StopNavigation : NavigationEvent()
    object CompleteTrip : NavigationEvent()
    data class SetDestination(val location: LocationData) : NavigationEvent()
    data class UpdateLocation(val location: LocationData) : NavigationEvent()
    object GpsSignalLost : NavigationEvent()
    object GpsSignalRestored : NavigationEvent()
} 