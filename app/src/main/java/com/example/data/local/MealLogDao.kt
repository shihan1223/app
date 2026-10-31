package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY mealNumber ASC")
    fun getMealLogsForDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY mealNumber ASC")
    suspend fun getMealLogsForDateDirect(date: String): List<MealLog>

    @Query("SELECT * FROM meal_logs WHERE date = :date AND mealNumber = :mealNumber LIMIT 1")
    suspend fun getMealLog(date: String, mealNumber: Int): MealLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: MealLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<MealLog>)

    @Update
    suspend fun update(log: MealLog)

    @Query("SELECT * FROM meal_logs ORDER BY date DESC, mealNumber ASC LIMIT 500")
    fun getAllHistory(): Flow<List<MealLog>>

    @Query("SELECT COUNT(*) FROM meal_logs WHERE date = :date AND status = 'COMPLETED'")
    fun getCompletedMealsCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM meal_logs WHERE date = :date AND status = 'COMPLETED'")
    suspend fun getCompletedMealsCountDirect(date: String): Int
}
