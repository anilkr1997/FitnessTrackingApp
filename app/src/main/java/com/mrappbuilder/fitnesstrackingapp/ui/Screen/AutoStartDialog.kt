package com.mrappbuilder.fitnesstrackingapp.ui.Screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AutoStartDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Enable AutoStart") },
        text = { Text("To track your steps reliably, allow this app to auto-start after device reboot.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Enable")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Not now")
            }
        }
    )
}
