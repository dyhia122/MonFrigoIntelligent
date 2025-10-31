package com.example.myapplication

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Aliment::class], version = 1, exportSchema = false)
abstract class FrigoDatabase : RoomDatabase() {
    abstract fun alimentDao(): AlimentDao

    companion object {
        @Volatile
        private var INSTANCE: FrigoDatabase? = null  // Corrigé : était AppDatabase

        fun getDatabase(context: Context): FrigoDatabase {  // Corrigé : retour FrigoDatabase
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FrigoDatabase::class.java,  // Corrigé : était AppDatabase
                    "fridge_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}