package com.mrappbuilder.fitnesstrackingapp.HelperClass


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.core.net.toUri
import android.os.Build
import android.widget.Toast

fun Context.requestIgnoreBatteryOptimizations() {
    val pm = getSystemService(PowerManager::class.java)
    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            "package:$packageName".toUri()
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}



fun Context.openAutoStartSettings() {
    try {
        val brand = Build.BRAND.lowercase()

        val intent = when {
            brand.contains("xiaomi") -> Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            brand.contains("oppo") -> Intent().apply {
                component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            }
            brand.contains("vivo") -> Intent().apply {
                component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            }
            brand.contains("huawei") -> Intent().apply {
                component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            }
            else -> null
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Your device handles auto-start differently!", Toast.LENGTH_LONG).show()
        }

    } catch (e: Exception) {
        Toast.makeText(this, "Please enable AutoStart manually in Settings.", Toast.LENGTH_LONG).show()
    }
}

