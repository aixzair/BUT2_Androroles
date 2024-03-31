package com.example.androroles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UtilisateurDAO {
    @Query("SELECT * FROM utilisateurs")
    fun getAllUtilisateurs(): List<Utilisateur>

    @Insert
    fun insertUtilisateur(utilisateur: Utilisateur)

    @Query("DELETE FROM utilisateurs")
    suspend fun supprimerTousLesUtilisateurs()

    @Query("SELECT * FROM utilisateurs WHERE adresseMail = :email AND motDePasse = :password")
    suspend fun getUtilisateurByEmailAndPassword(email: String, password: String): Utilisateur?

}
