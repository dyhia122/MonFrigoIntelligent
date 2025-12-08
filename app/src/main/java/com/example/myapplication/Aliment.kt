package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aliments")
data class Aliment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val quantite: Int,
    val dateExpiration: String
)