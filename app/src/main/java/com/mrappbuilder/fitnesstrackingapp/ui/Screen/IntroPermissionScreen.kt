package com.mrappbuilder.fitnesstrackingapp.ui.Screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun IntroPermissionScreen(onContinue: () -> Unit) {

    val context = LocalContext.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    val activityPerm = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)
    val notifPerm =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        else null

    //  Auto navigate when ALL required permissions are granted
    LaunchedEffect(activityPerm.status, notifPerm?.status) {
        if (activityPerm.status.isGranted &&
            (notifPerm == null || notifPerm.status.isGranted)
        ) {
            onContinue()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text("Welcome",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("We need a couple of permissions to track your steps accurately in background.")
            Spacer(modifier = Modifier.height(24.dp))

            PermissionCard("Activity Recognition", activityPerm.status.isGranted)
            if (notifPerm != null)
                PermissionCard("Notifications", notifPerm.status.isGranted)
        }

        Button(
            onClick = {
                activityPerm.launchPermissionRequest()
                notifPerm?.launchPermissionRequest()

                //  When user denied forever, show the Settings popup
                if (!activityPerm.status.isGranted &&
                    !activityPerm.status.shouldShowRationale
                ) {
                    showSettingsDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            //  Enabled only while permissions are missing
            enabled = !activityPerm.status.isGranted ||
                    (notifPerm != null && !notifPerm.status.isGranted)
        ) {
            Text("Allow Permission")
        }
    }

    // Dialog — permanently denied permission handling
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Please enable Activity Recognition from Settings to continue.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    granted: Boolean
) {
    ElevatedCard(
        Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, Modifier.weight(1f))
            AssistChip(
                onClick = {},
                label = {
                    Text(if (granted) "Granted " else "Required ")
                }
            )
        }
    }
}
