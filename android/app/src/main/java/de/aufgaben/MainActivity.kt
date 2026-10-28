package de.aufgaben

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Eine einzelne Aufgabe – entspricht einem Eintrag in aufgaben.py. */
data class Aufgabe(val text: String, val erledigt: Boolean, val erstellt: String)

/** Speichert die Aufgaben als JSON-Datei im privaten App-Speicher. */
class Speicher(context: Context) {
    private val datei = File(context.filesDir, "aufgaben.json")

    fun laden(): List<Aufgabe> {
        if (!datei.exists()) return emptyList()
        return try {
            val array = JSONArray(datei.readText())
            List(array.length()) { i ->
                val o = array.getJSONObject(i)
                Aufgabe(o.getString("text"), o.getBoolean("erledigt"), o.getString("erstellt"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun speichern(aufgaben: List<Aufgabe>) {
        val array = JSONArray()
        for (a in aufgaben) {
            array.put(
                JSONObject()
                    .put("text", a.text)
                    .put("erledigt", a.erledigt)
                    .put("erstellt", a.erstellt)
            )
        }
        datei.writeText(array.toString(2))
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val speicher = Speicher(this)
        setContent {
            val farben = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            MaterialTheme(colorScheme = farben) {
                AufgabenBildschirm(speicher)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AufgabenBildschirm(speicher: Speicher) {
    val aufgaben = remember { mutableStateListOf<Aufgabe>().apply { addAll(speicher.laden()) } }
    var neuerText by remember { mutableStateOf("") }

    fun aendern(aktion: () -> Unit) {
        aktion()
        speicher.speichern(aufgaben)
    }

    fun hinzufuegen() {
        val text = neuerText.trim()
        if (text.isEmpty()) return
        val jetzt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY).format(Date())
        aendern { aufgaben.add(Aufgabe(text, false, jetzt)) }
        neuerText = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val offen = aufgaben.count { !it.erledigt }
                    Text("Aufgaben ($offen offen)")
                },
                actions = {
                    TextButton(onClick = { aendern { aufgaben.removeAll { it.erledigt } } }) {
                        Text("Erledigte entfernen")
                    }
                }
            )
        }
    ) { innen ->
        Column(
            Modifier
                .padding(innen)
                .imePadding()
                .fillMaxSize()
        ) {
            // Eingabezeile für neue Aufgaben
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = neuerText,
                    onValueChange = { neuerText = it },
                    label = { Text("Neue Aufgabe") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { hinzufuegen() }),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { hinzufuegen() }) { Text("Hinzufügen") }
            }

            if (aufgaben.isEmpty()) {
                Text(
                    "Keine Aufgaben",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Liste der Aufgaben
            LazyColumn(verticalArrangement = Arrangement.Top) {
                itemsIndexed(aufgaben) { index, aufgabe ->
                    AufgabenZeile(
                        aufgabe = aufgabe,
                        onUmschalten = {
                            aendern { aufgaben[index] = aufgabe.copy(erledigt = !aufgabe.erledigt) }
                        },
                        onLoeschen = { aendern { aufgaben.removeAt(index) } }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun AufgabenZeile(aufgabe: Aufgabe, onUmschalten: () -> Unit, onLoeschen: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onUmschalten)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = aufgabe.erledigt, onCheckedChange = { onUmschalten() })
        Column(Modifier.weight(1f)) {
            Text(
                aufgabe.text,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (aufgabe.erledigt) TextDecoration.LineThrough else null
            )
            Text(aufgabe.erstellt, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onLoeschen) {
            Icon(Icons.Filled.Delete, contentDescription = "Löschen")
        }
    }
}
