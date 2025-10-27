package com.mrappbuilder.fitnesstrackingapp.ui.Screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.mrappbuilder.fitnesstrackingapp.HelperClass.requestIgnoreBatteryOptimizations

@Composable
fun AskBatteryOptimizationOnce(
    shouldAsk: Boolean,
    onAsked: () -> Unit
) {
    if (!shouldAsk) return
    var show by remember { mutableStateOf(true) }
    if (!show) return

    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = { show = false; onAsked() },
        title = { Text("Keep step tracking running") },
        text = { Text("Allow the app to run without battery restrictions for reliable background step counting.") },
        confirmButton = {
            TextButton(onClick = {
                show = false
                onAsked()
                ctx.requestIgnoreBatteryOptimizations()
            }) { Text("Allow") }
        },
        dismissButton = {
            TextButton(onClick = { show = false; onAsked() }) { Text("Not now") }
        }
    )
}
