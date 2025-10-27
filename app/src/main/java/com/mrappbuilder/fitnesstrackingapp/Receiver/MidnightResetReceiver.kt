package com.mrappbuilder.fitnesstrackingapp.Receiver


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mrappbuilder.fitnesstrackingapp.service.StepService

class MidnightResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Restart service to re-base the step counter for the new day
        context.startForegroundService(Intent(context, StepService::class.java))
    }
}
