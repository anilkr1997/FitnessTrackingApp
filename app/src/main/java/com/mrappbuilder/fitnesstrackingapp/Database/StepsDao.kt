package com.mrappbuilder.fitnesstrackingapp.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailySteps)


    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    fun getAll(): Flow<List<DailySteps>>


    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailySteps?






//    @Query("SELECT * FROM daily_steps WHERE date = :today")
//    suspend fun getSteps(today: String): DailySteps?

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(record: DailySteps)

    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    fun getHistory(): Flow<List<DailySteps>>
}
