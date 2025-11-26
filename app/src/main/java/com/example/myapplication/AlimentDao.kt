package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlimentDao {

    @Query("SELECT * FROM aliments")
    fun getAllAliments(): Flow<List<Aliment>>

    @Query("SELECT * FROM aliments")
    fun getAllNow(): List<Aliment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aliment: Aliment)

    @Update
    suspend fun update(aliment: Aliment)

    @Delete
    suspend fun delete(aliment: Aliment)
}
