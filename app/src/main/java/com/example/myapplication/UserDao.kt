package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE nom = :nom LIMIT 1")
    suspend fun getUserByNom(nom: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}