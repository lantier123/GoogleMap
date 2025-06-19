package com.example.mapdemo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapdemo.data.LocationData
import com.example.mapdemo.data.NavigationEvent
import com.example.mapdemo.ui.components.*
import com.example.mapdemo.viewmodel.NavigationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: NavigationViewModel = viewModel { NavigationViewModel(context) }
    
    val navigationState by viewModel.navigationState.collectAsState()
    var mapLoaded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = remember {
        CameraPositionState().apply {
            position = CameraPosition.fromLatLngZoom(
                LatLng(37.7749, -122.4194), // Default to San Francisco
                10f
            )
        }
    }
    
    LaunchedEffect(navigationState.currentLocation) {
        navigationState.currentLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            )
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
                val locationData = LocationData(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
                viewModel.handleEvent(NavigationEvent.SetDestination(locationData))
            },
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapStyleOptions = null
            ),
            onMapLoaded = {
                mapLoaded = true
            }
        ) {
            // Current location marker
            navigationState.currentLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location.toLatLng()),
                    title = "Current Location",
                    snippet = "You are here"
                )
            }
            
            // Destination marker
            navigationState.destination?.let { destination ->
                Marker(
                    state = MarkerState(position = destination.toLatLng()),
                    title = "Destination",
                    snippet = "Selected destination"
                )
            }
            
            // Path polyline
            if (navigationState.pathPoints.isNotEmpty()) {
                Polyline(
                    points = navigationState.pathPoints,
                    color = Color.Blue,
                    width = 8f
                )
            }
        }
        
        // Loading indicator
        if (!mapLoaded) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        // GPS Signal Loss Notification
        if (navigationState.gpsSignalLost) {
            GpsSignalLossNotification()
        }
        
        // Navigation Controls
        if (mapLoaded) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tap on map to select destination",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                NavigationControls(
                    modifier = Modifier.fillMaxWidth(),
                    navigationState = navigationState,
                    onStartNavigation = {
                        viewModel.handleEvent(NavigationEvent.StartNavigation)
                    },
                    onStopNavigation = {
                        viewModel.handleEvent(NavigationEvent.StopNavigation)
                    }
                )
            }
        }
        
        // Trip Summary
        if (navigationState.tripData.isCompleted) {
            TripSummaryDialog(
                tripData = navigationState.tripData,
                onDismiss = {
                    viewModel.handleEvent(NavigationEvent.StopNavigation)
                }
            )
        }
        
        // Navigation Info Panel
        if (navigationState.isNavigating) {
            NavigationInfoPanel(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                distanceToDestination = navigationState.distanceToDestination,
                estimatedTime = navigationState.estimatedTimeToDestination,
                formatDistance = viewModel::formatDistance,
                formatTime = viewModel::formatTime
            )
        }
    }
} 