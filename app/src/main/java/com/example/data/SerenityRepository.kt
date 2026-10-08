package com.example.data

import kotlinx.coroutines.flow.Flow

class SerenityRepository(private val dao: SerenityDao) {
    val allCheckIns: Flow<List<StressCheckIn>> = dao.getAllCheckIns()
    val allSessions: Flow<List<MindfulSession>> = dao.getAllSessions()
    val totalMindfulSeconds: Flow<Int?> = dao.getTotalMindfulSeconds()

    suspend fun saveCheckIn(checkIn: StressCheckIn): Long {
        return dao.insertCheckIn(checkIn)
    }

    suspend fun removeCheckIn(checkIn: StressCheckIn) {
        dao.deleteCheckIn(checkIn)
    }

    suspend fun saveSession(session: MindfulSession): Long {
        return dao.insertSession(session)
    }
}
