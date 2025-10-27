package com.mrappbuilder.fitnesstrackingapp.ViewModel



import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StepViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepsViewModel::class.java)) {
            return StepsViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
