package com.mrappbuilder.fitnesstrackingapp

import android.app.Application
import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.mrappbuilder.fitnesstrackingapp.Database.AppDatabase
import com.mrappbuilder.fitnesstrackingapp.Database.GoalRepository
import com.mrappbuilder.fitnesstrackingapp.Database.StepRepository
import com.mrappbuilder.fitnesstrackingapp.RepImplementation.GoalRepositoryImpl
import com.mrappbuilder.fitnesstrackingapp.RepImplementation.StepRepositoryImpl
private val Context.dataStore by preferencesDataStore("settings")

class FitApp : Application() {
    lateinit var stepRepo: StepRepository
    lateinit var goalRepo: GoalRepository
    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(
            this, AppDatabase::class.java, "steps.db"
        ).fallbackToDestructiveMigration().build()

        stepRepo = StepRepositoryImpl(db.stepsDao())
        goalRepo = GoalRepositoryImpl(dataStore)
    }

}