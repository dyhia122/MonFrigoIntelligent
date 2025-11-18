package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "corbeille")
data class CorbeilleAliment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val quantite: Int,
    val dateExpiration: String,
    val dateSuppression: String // pour savoir quand il a été supprimé
)
