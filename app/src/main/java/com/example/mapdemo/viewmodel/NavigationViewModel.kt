package com.example.mapdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapdemo.data.*
import com.example.mapdemo.services.LocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class NavigationViewModel(private val context: Context) : ViewModel() {
    
    private val locationService = LocationService(context)
    
    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()
    
    private var locationJob: kotlinx.coroutines.Job? = null
    
    init {
        startLocationUpdates()
    }
    
    private fun startLocationUpdates() {
        locationJob = viewModelScope.launch {
            locationService.getLocationUpdates()
                .catch { exception ->
                    // Handle location errors
                }
                .collect { locationData ->
                    updateLocation(locationData)
                }
        }
    }
    
    private fun updateLocation(locationData: LocationData) {
        val currentState = _navigationState.value
        
        // Check GPS signal quality
        val gpsSignalLost = !locationService.isGpsSignalGood(locationData)
        
        val newState = currentState.copy(
            currentLocation = locationData,
            gpsSignalLost = gpsSignalLost
        )
        
        _navigationState.value = newState
        
        // If navigating, update path and check if destination reached
        if (currentState.isNavigating && currentState.destination != null) {
            updateNavigationProgress(locationData, currentState.destination!!)
        }
    }
    
    private fun updateNavigationProgress(currentLocation: LocationData, destination: LocationData) {
        val distance = locationService.calculateDistance(currentLocation, destination)
        val bearing = locationService.calculateBearing(currentLocation, destination)
        
        // Check if destination reached (within 20 meters)
        if (distance <= 20f) {
            completeTrip()
            return
        }
        
        val currentState = _navigationState.value
        val newPathPoints = currentState.pathPoints + currentLocation.toLatLng()
        
        // Estimate time to destination (assuming average speed of 5 m/s)
        val estimatedTimeSeconds = (distance / 5f).toLong()
        
        _navigationState.value = currentState.copy(
            pathPoints = newPathPoints,
            distanceToDestination = distance,
            estimatedTimeToDestination = estimatedTimeSeconds
        )
    }
    
    fun handleEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.SetDestination -> {
                setDestination(event.location)
            }
            NavigationEvent.StartNavigation -> {
                startNavigation()
            }
            NavigationEvent.StopNavigation -> {
                stopNavigation()
            }
            NavigationEvent.CompleteTrip -> {
                completeTrip()
            }
            NavigationEvent.GpsSignalLost -> {
                _navigationState.value = _navigationState.value.copy(gpsSignalLost = true)
            }
            NavigationEvent.GpsSignalRestored -> {
                _navigationState.value = _navigationState.value.copy(gpsSignalLost = false)
            }
            is NavigationEvent.UpdateLocation -> {
                updateLocation(event.location)
            }
        }
    }
    
    private fun setDestination(location: LocationData) {
        val currentState = _navigationState.value
        _navigationState.value = currentState.copy(
            destination = location
        )
    }
    
    private fun startNavigation() {
        val currentState = _navigationState.value
        val currentLocation = currentState.currentLocation
        val destination = currentState.destination
        
        if (currentLocation != null && destination != null) {
            val tripData = TripData(
                startLocation = currentLocation,
                destination = destination,
                startTime = System.currentTimeMillis(),
                pathPoints = listOf(currentLocation.toLatLng())
            )
            
            _navigationState.value = currentState.copy(
                isNavigating = true,
                tripData = tripData,
                pathPoints = listOf(currentLocation.toLatLng())
            )
        }
    }
    
    private fun stopNavigation() {
        val currentState = _navigationState.value
        _navigationState.value = currentState.copy(
            isNavigating = false
        )
    }
    
    private fun completeTrip() {
        val currentState = _navigationState.value
        val tripData = currentState.tripData
        
        if (tripData.startLocation != null) {
            val endTime = System.currentTimeMillis()
            val elapsedTime = endTime - tripData.startTime
            
            // Calculate total distance traveled
            val totalDistance = calculateTotalDistance(currentState.pathPoints)
            
            val completedTripData = tripData.copy(
                endTime = endTime,
                totalDistance = totalDistance,
                pathPoints = currentState.pathPoints,
                isCompleted = true
            )
            
            _navigationState.value = currentState.copy(
                isNavigating = false,
                tripData = completedTripData
            )
        }
    }
    
    private fun calculateTotalDistance(pathPoints: List<LatLng>): Float {
        if (pathPoints.size < 2) return 0f
        
        var totalDistance = 0f
        for (i in 0 until pathPoints.size - 1) {
            val from = LocationData(pathPoints[i].latitude, pathPoints[i].longitude)
            val to = LocationData(pathPoints[i + 1].latitude, pathPoints[i + 1].longitude)
            totalDistance += locationService.calculateDistance(from, to)
        }
        return totalDistance
    }
    
    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, secs)
            else -> String.format("%02d:%02d", minutes, secs)
        }
    }
    
    fun formatDistance(meters: Float): String {
        return when {
            meters >= 1000 -> String.format("%.1f km", meters / 1000)
            else -> String.format("%.0f m", meters)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
} 