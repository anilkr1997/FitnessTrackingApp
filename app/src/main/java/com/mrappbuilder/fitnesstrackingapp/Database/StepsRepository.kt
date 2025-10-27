package com.mrappbuilder.fitnesstrackingapp.Database

import android.content.Context
import com.mrappbuilder.fitnesstrackingapp.DataClass.StepRecord
import com.mrappbuilder.fitnesstrackingapp.HelperClass.Uttils.today
import kotlinx.coroutines.flow.Flow


interface StepRepository {
    suspend fun getTodaySteps(): Int
    suspend fun saveTodaySteps(steps: Int)
    fun history(): Flow<List<StepRecord>>


}