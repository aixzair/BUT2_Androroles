package com.example.androroles

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androroles.databinding.ActivityConnexionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Connexion : AppCompatActivity() {
    private lateinit var binding: ActivityConnexionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnexionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Appeler la fonction pour vider et insérer un nouvel utilisateur
        viderEtInsererUtilisateur()

        val but = findViewById<Button>(R.id.button)
        val label = findViewById<TextView>(R.id.textView3)
        val id = findViewById<EditText>(R.id.editTextAdresseMail)
        val mdp = findViewById<EditText>(R.id.editTextMotDePasse)

        but.setOnClickListener {
            val email = id.text.toString()
            val motDePasse = mdp.text.toString()

            // Utiliser une coroutine pour exécuter des opérations de base de données de manière asynchrone
            lifecycleScope.launch {
                val utilisateur = withContext(Dispatchers.IO) {
                    Base.getInstance(applicationContext).UtilisateurDAO().getUtilisateurByEmailAndPassword(email, motDePasse)
                }

                if (utilisateur != null) {
                    // Afficher un message de connexion réussie
                    label.text = "Connexion réussie"
                } else {
                    // Afficher un message de connexion échouée
                   label.text = "Connexion échouée"
                }
            }
        }
    }

    private fun viderEtInsererUtilisateur() {
        // Utiliser une coroutine pour exécuter des opérations de base de données de manière asynchrone
        lifecycleScope.launch {
            // Supprimer tous les utilisateurs de la table Utilisateur
            withContext(Dispatchers.IO) {
                Base.getInstance(applicationContext).UtilisateurDAO().supprimerTousLesUtilisateurs()
            }

            // Insérer un nouvel utilisateur dans la table Utilisateur
            val utilisateur = Utilisateur(
                id = 1,
                adresseMail = "jeanpierre@unicaen.fr",
                motDePasse = "RequeteDansLaVue",
                nom = "Jeanpierre",
                prenom = "Laurent"
            )
            withContext(Dispatchers.IO) {
                Base.getInstance(applicationContext).UtilisateurDAO().insertUtilisateur(utilisateur)
            }
        }
    }
}