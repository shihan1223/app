package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MedicineLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineLogDao {
    @Query("SELECT * FROM medicine_logs WHERE scheduledDate = :date ORDER BY scheduledTime ASC")
    fun getMedicineLogsForDate(date: String): Flow<List<MedicineLog>>

    @Query("SELECT * FROM medicine_logs WHERE scheduledDate = :date ORDER BY scheduledTime ASC")
    suspend fun getMedicineLogsForDateDirect(date: String): List<MedicineLog>

    @Query("SELECT * FROM medicine_logs WHERE scheduledDate = :date AND medicineId = :medicineId AND scheduledTime = :time LIMIT 1")
    suspend fun getMedicineLog(date: String, medicineId: Long, time: String): MedicineLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: MedicineLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<MedicineLog>)

    @Update
    suspend fun update(log: MedicineLog)

    @Query("SELECT * FROM medicine_logs ORDER BY scheduledDate DESC, scheduledTime ASC LIMIT 500")
    fun getAllHistory(): Flow<List<MedicineLog>>

    @Query("SELECT COUNT(*) FROM medicine_logs WHERE scheduledDate = :date AND status = 'COMPLETED'")
    fun getTakenMedsCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM medicine_logs WHERE scheduledDate = :date AND status = 'COMPLETED'")
    suspend fun getTakenMedsCountDirect(date: String): Int
}
