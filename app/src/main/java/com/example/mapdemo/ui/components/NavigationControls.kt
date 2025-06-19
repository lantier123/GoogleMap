package com.example.mapdemo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapdemo.data.NavigationState

@Composable
fun NavigationControls(
    modifier: Modifier = Modifier,
    navigationState: NavigationState,
    onStartNavigation: () -> Unit,
    onStopNavigation: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Destination Status
            if (navigationState.destination != null) {
                Text(
                    text = "Destination Selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text(
                    text = "Tap on map to select destination",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Navigation Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (navigationState.isNavigating) {
                    // Stop Navigation Button
                    Button(
                        onClick = onStopNavigation,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Stop Navigation",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Start Navigation Button
                    Button(
                        onClick = onStartNavigation,
                        enabled = navigationState.destination != null && navigationState.currentLocation != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Start Navigation",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Current Status
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when {
                    navigationState.isNavigating -> "Navigating to destination..."
                    navigationState.destination != null -> "Ready to navigate"
                    else -> "Select a destination"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 