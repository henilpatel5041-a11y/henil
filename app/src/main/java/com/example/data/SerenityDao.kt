package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SerenityDao {
    @Query("SELECT * FROM stress_check_ins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<StressCheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: StressCheckIn): Long

    @Delete
    suspend fun deleteCheckIn(checkIn: StressCheckIn)

    @Query("SELECT * FROM mindful_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<MindfulSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: MindfulSession): Long

    @Query("SELECT SUM(durationSeconds) FROM mindful_sessions")
    fun getTotalMindfulSeconds(): Flow<Int?>
}
