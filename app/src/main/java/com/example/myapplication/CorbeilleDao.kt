package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CorbeilleDao {

    @Query("SELECT * FROM corbeille ORDER BY dateSuppression DESC")
    fun getAllCorbeille(): Flow<List<CorbeilleAliment>>

    @Query("SELECT * FROM corbeille ORDER BY dateSuppression DESC")
    fun getAllCorbeilleNow(): List<CorbeilleAliment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(corbeilleAliment: CorbeilleAliment)

    @Delete
    suspend fun delete(corbeilleAliment: CorbeilleAliment)

    @Query("DELETE FROM corbeille")
    suspend fun clearAll()
}