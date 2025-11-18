package com.example.myapplication

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CorbeilleDao {

    @Query("SELECT * FROM corbeille ORDER BY dateSuppression DESC")
    fun getAllCorbeille(): Flow<List<CorbeilleAliment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(corbeilleAliment: CorbeilleAliment)

    @Delete
    suspend fun delete(corbeilleAliment: CorbeilleAliment)

    @Query("DELETE FROM corbeille")
    suspend fun clearAll()
}
