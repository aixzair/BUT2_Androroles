package com.example.androroles

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utilisateurs")
data class Utilisateur(
    @PrimaryKey val id: Int,
    val adresseMail: String,
    val motDePasse: String,
    val nom: String,
    val prenom: String
)