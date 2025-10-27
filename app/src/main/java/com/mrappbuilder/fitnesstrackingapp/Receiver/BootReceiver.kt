package com.mrappbuilder.fitnesstrackingapp.Receiver


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mrappbuilder.fitnesstrackingapp.service.StepService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startForegroundService(Intent(context, StepService::class.java))
        }
    }
}
