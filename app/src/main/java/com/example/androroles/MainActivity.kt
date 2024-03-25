package com.example.androroles

import android.Manifest
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.androroles.api_sae3.Autorisation
import com.example.androroles.ui.theme.AndrorolesTheme

class MainActivity : ComponentActivity() {
    val autorisation = mutableStateOf<Autorisation>(Autorisation.INCONNU)

    val res = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        autorisation.value =
            if (result)
                Autorisation.AUTORISE
            else
                Autorisation.REFUSE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndrorolesTheme {
                Code();
            }
        }
    }

    @Composable
    fun App(etat: MutableState<Autorisation>) {
        if (checkSelfPermission(android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            etat.value = Autorisation.AUTORISE;
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.INTERNET)) {
                Column(Modifier.fillMaxSize()) {
                    Text("Autoriser les notifications vous permettra d'utiliser l'application")
                    Spacer(modifier = Modifier.size(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { autorisation.value = Autorisation.REFUSE }) {
                            Text("Interdire")
                        }
                        Button(onClick = { res.launch(Manifest.permission.INTERNET) }) {
                            Text("Demander")
                        }
                    }
                }
            } else {
                res.launch(android.Manifest.permission.INTERNET)
            }
        }
    }

    fun Autre() {
        val mgr = ContextCompat.getSystemService(ConnectivityManager::class.java)

    }
}


@Composable
@Preview(showBackground = true)
fun Code() {
    val modifierRow = Modifier.fillMaxWidth();

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ){
            Row(modifierRow) {
                Text("Choix palanque");
            }
        }
    }
}


