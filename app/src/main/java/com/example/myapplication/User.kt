package com.example.myapplication

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["nom"], unique = true)]  // Assure l'unicité des noms d'utilisateur
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val motDePasseHash: String,  // Renommé pour stocker le hash au lieu du mot de passe en clair
    val salt: String  // Sel pour le hachage
)