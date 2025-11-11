package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Aliment::class], version = 1, exportSchema = false)
abstract class FrigoDatabase : RoomDatabase() {

    abstract fun alimentDao(): AlimentDao

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
                    // 🔄 Option utile : recrée la DB si le schéma change
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
