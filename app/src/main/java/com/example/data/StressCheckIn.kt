package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stress_check_ins")
data class StressCheckIn(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val stressLevel: Int, // 1 to 10
    val mood: String, // e.g. "Peaceful", "Anxious", "Overwhelmed", "Restless", "Grateful"
    val note: String = ""
)
