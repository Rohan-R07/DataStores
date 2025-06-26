package com.example.datastores

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.datastores.ui.theme.DataStoresTheme
import kotlinx.coroutines.launch
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.example.datastores.ui.theme.DarkColorScheme
import com.example.datastores.ui.theme.LightColorScheme


class MainActivity : ComponentActivity() {

    val preference = lazy {
        PrefUtils(applicationContext)
    }

    val darkPref = lazy {
        PrefUtilsDark(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoresTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreRepresentation(
                        preference = preference.value,
                        darkThemPref = darkPref.value ,
                        innerPadding,
                        contex = applicationContext)
                }
            }
        }
    }
}


@Composable
fun DataStoreRepresentation(
    preference: PrefUtils,
    darkThemPref : PrefUtilsDark,
    innnerPaddingValues: PaddingValues,
    contex: Context
) {

    val courutionScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(innnerPaddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }


        var fetchKey by remember { mutableStateOf("") }

        Spacer(Modifier.padding(20.dp))

        TextField(
            value = key,
            onValueChange = { key = it },
            placeholder = { Text("Key") },
            label = { Text("key") }
        )

        Spacer(Modifier.padding(20.dp))

        TextField(
            value = value,
            onValueChange = { value = it },
            placeholder = { Text("value") },
            label = { Text("value") }
        )

        Button(
            onClick = {
                courutionScope.launch {
                    preference.saveData(key, value)
                }
            }
        ) {
            Text("Save Data")
        }

        Spacer(Modifier.padding(20.dp))

        TextField(
            fetchKey,
            onValueChange = { fetchKey = it },
            label = { Text("Find With Key") },
            placeholder = {
                Text("Enter Key")
            }
        )

        Spacer(Modifier.padding(10.dp))

        var fetchText by remember { mutableStateOf("") }
//

        Text(fetchText)

        val listings by preference.getAllPreferences().collectAsState(initial = emptyMap())


        val isDarkTheme by darkThemPref.readDarkMode().collectAsState(initial = false)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(listings.entries.toList()) { items ->
                Text(items.value.toString())
            }

        }


        Switch(
            checked = isDarkTheme,
            onCheckedChange = {
                courutionScope.launch {
                    darkThemPref.setDarkMode(it)
                }
            }
        )

        if (isDarkTheme){

        }
        else{
            LightColorScheme
        }

    }
}