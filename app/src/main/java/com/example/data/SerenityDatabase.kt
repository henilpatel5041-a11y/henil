package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [StressCheckIn::class, MindfulSession::class],
    version = 1,
    exportSchema = false
)
abstract class SerenityDatabase : RoomDatabase() {
    abstract fun serenityDao(): SerenityDao

    companion object {
        @Volatile
        private var INSTANCE: SerenityDatabase? = null

        fun getDatabase(context: Context): SerenityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SerenityDatabase::class.java,
                    "serenity_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
