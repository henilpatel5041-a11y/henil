package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mindful_sessions")
data class MindfulSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionType: String, // "Breathing (4-7-8)", "Box Breathing", "Singing Bowl", "Nature Soundscape"
    val durationSeconds: Int,
    val cyclesCompleted: Int = 0
)
