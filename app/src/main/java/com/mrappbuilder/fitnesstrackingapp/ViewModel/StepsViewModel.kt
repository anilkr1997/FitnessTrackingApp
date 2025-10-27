package com.mrappbuilder.fitnesstrackingapp.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.provider.SyncStateContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrappbuilder.fitnesstrackingapp.DataClass.StepRecord

import com.mrappbuilder.fitnesstrackingapp.FitApp
import com.mrappbuilder.fitnesstrackingapp.HelperClass.Uttils
import com.mrappbuilder.fitnesstrackingapp.service.StepService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StepsViewModel(
  private val app: Application
) : AndroidViewModel(app) {

    private val stepRepo = (app as FitApp).stepRepo
    private val goalRepo = (app as FitApp).goalRepo

    // Live UI state
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    val goal: StateFlow<Int> = goalRepo.goalFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(500), 100
    )

    val history: StateFlow<List<StepRecord>> = stepRepo.history()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // service binding
    @SuppressLint("StaticFieldLeak")
    private var service: StepService? = null
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val b = binder as StepService.LocalBinder
            service = b.getService()
            viewModelScope.launch {
                service!!.steps.collect { _steps.value = it }
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) { service = null }
    }

    fun startAndBindService() {

        val intent = Intent(app, StepService::class.java)
        app.startForegroundService(intent)
        app.bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    fun setGoal(value: Int) {
        viewModelScope.launch { goalRepo.setGoal(value.coerceIn(10, 50000)) }
    }
}
