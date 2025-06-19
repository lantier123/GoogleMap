package com.example.mapdemo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsHandler(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    
    when {
        locationPermissionState.status.isGranted -> {
            content()
        }
        locationPermissionState.status.shouldShowRationale -> {
            PermissionRationaleDialog(
                onRequestPermission = { locationPermissionState.launchPermissionRequest() }
            )
        }
        else -> {
            PermissionRequestScreen(
                onRequestPermission = { locationPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This app needs location access to provide navigation features. Please grant location permission to continue.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Grant Permission",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PermissionRationaleDialog(
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "Location Permission",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Location permission is required for navigation features. Without this permission, the app cannot provide accurate navigation.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(
                    text = "Grant Permission",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { }) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
} 