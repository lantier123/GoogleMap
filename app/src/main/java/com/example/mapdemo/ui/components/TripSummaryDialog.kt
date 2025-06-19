package com.example.mapdemo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mapdemo.data.TripData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun TripSummaryDialog(
    tripData: TripData,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Trip Completed!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Trip Map
                if (tripData.pathPoints.isNotEmpty()) {
                    val cameraPosition = remember {
                        CameraPosition.fromLatLngZoom(
                            tripData.pathPoints.first(),
                            15f
                        )
                    }
                    
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        cameraPositionState = rememberCameraPositionState {
                            position = cameraPosition
                        },
                        properties = MapProperties(
                            isMyLocationEnabled = false
                        )
                    ) {
                        // Path polyline
                        Polyline(
                            points = tripData.pathPoints,
                            color = Color.Blue,
                            width = 8f
                        )
                        
                        // Start marker
                        tripData.startLocation?.let { start ->
                            Marker(
                                state = MarkerState(position = start.toLatLng()),
                                title = "Start",
                                snippet = "Trip start point"
                            )
                        }
                        
                        // End marker
                        tripData.destination?.let { dest ->
                            Marker(
                                state = MarkerState(position = dest.toLatLng()),
                                title = "Destination",
                                snippet = "Trip end point"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Trip Statistics
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TripStatRow(
                        label = "Total Distance",
                        value = formatDistance(tripData.totalDistance)
                    )
                    
                    TripStatRow(
                        label = "Elapsed Time",
                        value = formatElapsedTime(tripData.startTime, tripData.endTime)
                    )
                    
                    TripStatRow(
                        label = "Average Speed",
                        value = calculateAverageSpeed(tripData.totalDistance, tripData.startTime, tripData.endTime)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TripStatRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatDistance(meters: Float): String {
    return when {
        meters >= 1000 -> String.format("%.1f km", meters / 1000)
        else -> String.format("%.0f m", meters)
    }
}

private fun formatElapsedTime(startTime: Long, endTime: Long): String {
    val elapsedSeconds = (endTime - startTime) / 1000
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    
    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%02d:%02d", minutes, seconds)
    }
}

private fun calculateAverageSpeed(distance: Float, startTime: Long, endTime: Long): String {
    val elapsedHours = (endTime - startTime) / 1000.0 / 3600.0
    val speedKmh = if (elapsedHours > 0) (distance / 1000) / elapsedHours else 0.0
    return String.format("%.1f km/h", speedKmh)
} 