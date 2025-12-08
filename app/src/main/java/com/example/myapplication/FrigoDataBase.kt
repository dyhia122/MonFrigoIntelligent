package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Aliment::class, CorbeilleAliment::class], version = 2, exportSchema = false)
abstract class FrigoDatabase : RoomDatabase() {

    abstract fun alimentDao(): AlimentDao
    abstract fun corbeilleDao(): CorbeilleDao

    companion object {
        @Volatile
        private var INSTANCE: FrigoDatabase? = null

        fun getDatabase(context: Context): FrigoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FrigoDatabase::class.java,
                    "fridge_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}