package com.mrappbuilder.fitnesstrackingapp.RepImplementation

import com.mrappbuilder.fitnesstrackingapp.DataClass.StepRecord
import com.mrappbuilder.fitnesstrackingapp.Database.AppDatabase
import com.mrappbuilder.fitnesstrackingapp.Database.DailySteps
import com.mrappbuilder.fitnesstrackingapp.Database.StepRepository
import com.mrappbuilder.fitnesstrackingapp.Database.StepsDao
import com.mrappbuilder.fitnesstrackingapp.HelperClass.Uttils.today


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StepRepositoryImpl(private val dao: StepsDao) : StepRepository {


    override suspend fun getTodaySteps(): Int = dao.getByDate(today())?.steps ?: 0

    override suspend fun saveTodaySteps(steps: Int) {
        dao.upsert(DailySteps(date = today(), steps = steps))
    }

    override fun history(): Flow<List<StepRecord>> =
        dao.getHistory().map { list -> list.map { StepRecord(it.date, it.steps) } }


}
