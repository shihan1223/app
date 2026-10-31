package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FoodSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodScheduleDao {
    @Query("SELECT * FROM food_schedules ORDER BY mealNumber ASC")
    fun getAllFoodSchedules(): Flow<List<FoodSchedule>>

    @Query("SELECT * FROM food_schedules ORDER BY mealNumber ASC")
    suspend fun getAllFoodSchedulesDirect(): List<FoodSchedule>

    @Query("SELECT * FROM food_schedules WHERE id = :id LIMIT 1")
    suspend fun getFoodScheduleById(id: Long): FoodSchedule?

    @Query("SELECT * FROM food_schedules WHERE mealNumber = :mealNumber LIMIT 1")
    suspend fun getFoodScheduleByMealNumber(mealNumber: Int): FoodSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<FoodSchedule>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: FoodSchedule): Long

    @Update
    suspend fun update(schedule: FoodSchedule)

    @Query("DELETE FROM food_schedules")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM food_schedules")
    suspend fun getCount(): Int
}
