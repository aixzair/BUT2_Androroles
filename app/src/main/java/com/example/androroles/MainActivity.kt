package com.example.androroles

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androroles.api_sae3.Adherent
import com.example.androroles.api_sae3.Autorisation
import com.example.androroles.api_sae3.Palanque
import com.example.androroles.ui.theme.AndrorolesTheme
import org.json.JSONArray
import java.net.URL

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

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndrorolesTheme {
                AccesInternet(autorisation = autorisation)

                // Gestion de l'état de la connexion au réseau
                val connectivityManager = getSystemService(ConnectivityManager::class.java)
                val (connexion, setConnexion) =  remember { mutableStateOf(false) }
                DisposableEffect (connectivityManager) {
                    val networkCallback = object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            setConnexion(true)
                        }
                        override fun onLost(network: Network) {
                            setConnexion(false)
                        }
                    }
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                    onDispose {
                        connectivityManager.unregisterNetworkCallback(networkCallback)
                    }
                }

                if (connexion) {
                    Page()
                } else {
                    Text(text = "Activez votre connexion réseau")
                }
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            var palanques: List<Palanque> by remember { mutableStateOf(emptyList()) }

            Thread {
                val url = URL("https://dev-sae301grp4.users.info.unicaen.fr/api/palanques")
                val connexion = url.openConnection()

                connexion.connect()

                val flux = connexion.getInputStream()
                val palanquesJson = JSONArray(flux.bufferedReader().use { it.readText() })
                flux.close()

                val liste = mutableListOf<Palanque>()
                for (i in 0 until palanquesJson.length()) {
                    val palanqueJson = palanquesJson.getJSONObject(i)
                    liste.add(Palanque(
                        palanqueJson.getInt("PAL_ID"),
                        palanqueJson.getString("PLON_DATE")
                    ))
                }

                palanques = liste
            }.start()

            val (selection, setSelection) = remember { mutableIntStateOf(0) }

            AfficherSelection(palanques = palanques, selection = selection, setSelection = setSelection)
            AfficherPerssones(selection)
        }
    }
}

@Composable
fun AfficherSelection(palanques: List<Palanque>, selection: Int, setSelection: (Int) -> Unit) {
    var (derouler, setDerouler) = remember { mutableStateOf(false) }
    var (index, setIndex) = remember { mutableIntStateOf(-1) }

    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Choisissez votre palanquée : ")
        val textBox = if (index == -1) {
            "Faites votre choix"
        } else {
            "${palanques[index].pal_id}, ${palanques[index].plon_date}"
        }
        Box(
            modifier = Modifier
                .clickable(onClick = {
                    setDerouler(!derouler)
                })
                .border(1.dp, Color.Black)
                .background(Color.LightGray)
                .padding(start = 6.dp, end = 6.dp)
                .width(130.dp)
        ) {
            Text(
                text = textBox,
            )
        }
        DropdownMenu(
            expanded = derouler,
            onDismissRequest = { derouler = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            palanques.forEachIndexed { idx, palanque ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${palanque.pal_id}, ${palanque.plon_date}",
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    onClick = {
                        setSelection(palanque.pal_id)
                        setIndex(idx)
                        setDerouler(false)
                    }
                )
            }
        }
    }
}

@Composable
fun AfficherPerssones(selection: Int) {
    var adherents: List<Adherent> by remember { mutableStateOf(emptyList()) }

    Thread {
        val url = URL(Uri.Builder()
            .scheme("https")
            .authority("dev-sae301grp4.users.info.unicaen.fr")
            .appendPath("api")
            .appendPath("palanque")
            .appendPath("participants")
            .appendQueryParameter("id", selection.toString())
            .build().toString()
        )
        val connexion = url.openConnection()

        connexion.connect()

        val flux = connexion.getInputStream()
        val adherentsJson = JSONArray(flux.bufferedReader().use { it.readText() })
        flux.close()

        val liste = mutableListOf<Adherent>()
        for (i in 0 until adherentsJson.length()) {
            val adherentJson = adherentsJson.getJSONObject(i)
            liste.add(Adherent(
                adherentJson.getString("AD_NOM"),
                adherentJson.getString("AD_PRENOM")
            ))
        }

        adherents = liste
    }.start()

    if (selection != -1 ) {
        for (adherent in adherents) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${adherent.ad_nom} ${adherent.ad_prenom}")
            }
        }
    }
}
