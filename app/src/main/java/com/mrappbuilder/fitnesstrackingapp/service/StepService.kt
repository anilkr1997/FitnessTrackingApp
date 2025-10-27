package com.mrappbuilder.fitnesstrackingapp.service



import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mrappbuilder.fitnesstrackingapp.FitApp

import com.mrappbuilder.fitnesstrackingapp.R
import com.mrappbuilder.fitnesstrackingapp.Receiver.MidnightResetReceiver

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class StepService : Service(), SensorEventListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var sensorManager: SensorManager

    // Exposed to UI via Binder
    private val _steps = MutableStateFlow(0)
    val steps = _steps.asStateFlow()

    // goal live cache from DataStore
    @Volatile private var goalCache: Int = 6000

    private var baseCounter: Int? = null


    private val binder = LocalBinder()
    inner class LocalBinder : android.os.Binder() {
        fun getService(): StepService = this@StepService
    }
    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        createChannel()

        startForeground(1, buildNotification(0, reached = false))

        // register sensor
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // preload today steps
        val repos = (application as FitApp)
        serviceScope.launch {
            _steps.value = repos.stepRepo.getTodaySteps()
            goalCache = repos.goalRepo.getGoal()
            subscribeGoal()
            updateNotif(_steps.value)
        }

        // daily midnight alarm
        scheduleMidnightReset()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val total = event?.values?.getOrNull(0)?.toInt() ?: return
        if (baseCounter == null) baseCounter = total
        val stepsToday = (total - (baseCounter ?: total)).coerceAtLeast(0)



        // Update repos + state
        val repos = (application as FitApp)
        serviceScope.launch {
            _steps.value = stepsToday
            repos.stepRepo.saveTodaySteps(stepsToday)
            updateNotif(stepsToday)
        }

    }

    // ---- Notifications ----

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel("steps", "Step Tracking", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    private fun buildNotification(steps: Int, reached: Boolean): Notification {
        val progress = (steps * 100 / goalCache).coerceAtMost(100)
        val text = if (reached) "🎯 Goal Reached! $steps/$goalCache"
        else "Steps: $steps / $goalCache"

        return NotificationCompat.Builder(this, "steps")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tracking steps")
            .setContentText(text)
            .setProgress(100, progress, false)
            .setOngoing(!reached)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotif(steps: Int) {
        val reached = steps >= goalCache
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(1, buildNotification(steps, reached))
        if (reached) {
            pushInfo(4, "Great job!", "You reached $goalCache steps 🎉")
        }
    }

    private fun pushInfo(id: Int, title: String, msg: String) {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
            id, NotificationCompat.Builder(this, "steps")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        )
    }

    // ---- Goal flow ----
    private suspend fun subscribeGoal() {
        val repo = (application as FitApp).goalRepo
        serviceScope.launch {
            repo.goalFlow.collect { g ->
                goalCache = g
                updateNotif(_steps.value)
            }
        }
    }

    // ---- Midnight reset (re-register base) ----
    private fun scheduleMidnightReset() {
        val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MidnightResetReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        alarm.setRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pi
        )
    }
}
