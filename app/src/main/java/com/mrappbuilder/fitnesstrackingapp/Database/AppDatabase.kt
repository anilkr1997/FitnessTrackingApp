package com.mrappbuilder.fitnesstrackingapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailySteps::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepsDao(): StepsDao



}