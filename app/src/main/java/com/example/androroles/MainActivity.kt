package com.example.androroles

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androroles.api_sae3.Autorisation
import com.example.androroles.ui.theme.AndrorolesTheme

class MainActivity : ComponentActivity() {
    private val autorisation = mutableStateOf(Autorisation.INCONNU)

    private val res = registerForActivityResult(
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
                AccesInternet(autorisation = autorisation)
                Page();
            }
        }
    }

    @Composable
    fun AccesInternet(autorisation: MutableState<Autorisation>) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            == PackageManager.PERMISSION_GRANTED
        ) {
            autorisation.value = Autorisation.AUTORISE
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.INTERNET
            )) {
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
                res.launch(Manifest.permission.INTERNET)
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun Page() {
    val modifierRow = Modifier.fillMaxWidth()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            val liste = mutableListOf(
                "texte A",
                "texte B",
                "texte C"
            )
            val (selection, setSelection) = remember { mutableIntStateOf(0) }
            val personnes = arrayOf(
                "personne",
                "toi",
                "lui"
            )

            AfficherSelection(liste = liste, selection = selection, setSelection = setSelection)
            AfficherPerssones(personnes = personnes)
        }
    }
}

@Composable
fun AfficherSelection(liste: MutableList<String>, selection: Int, setSelection: (Int) -> Unit) {
    var (derouler, setDerouler) = remember { mutableStateOf(false) }

    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Choisissez votre palanquée : ")

        Box(
            modifier = Modifier
                .clickable(onClick = {
                    setDerouler(!derouler)
                })
                .border(1.dp, Color.Black)
                .background(Color.LightGray)
                .padding(start = 6.dp, end = 6.dp)
        ) {
            Text(
                text = liste[selection],
            )
        }

        DropdownMenu(
            expanded = derouler,
            onDismissRequest = { derouler = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            liste.forEachIndexed { index, valeur ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = valeur,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    onClick = {
                        setSelection(index)
                        setDerouler(false)
                    }
                )
            }
        }
    }
}

@Composable
fun AfficherPerssones(personnes: Array<String>) {
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        for (personne in personnes) {
            Row {
                Text(text = personne)
            }
        }
    }
}
