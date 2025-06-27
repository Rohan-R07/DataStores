package com.example.datastores

import android.content.Context
import android.hardware.camera2.params.BlackLevelPattern
import android.os.Build
import android.os.Bundle
import android.view.RoundedCorner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.example.datastores.ui.theme.DataStoresTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.datastore.dataStore
import kotlinx.coroutines.CoroutineScope


class MainActivity : ComponentActivity() {

    val preference = lazy {
        PrefUtils(applicationContext)
    }

    val darkPref = lazy {
        PrefUtilsDark(applicationContext)
    }
    val preferenceLanguage = lazy {
        PrefUtlisLang(applicationContext)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoresTheme {
                val isDarkTheme by darkPref.value.readDarkMode().collectAsState(initial = false)
                var backGroundColor by remember {
                    mutableStateOf(Color.White)
                }
                var textColor by remember { mutableStateOf(Color.Black) }
                val courutionScope = rememberCoroutineScope()

                val language by preferenceLanguage.value.readLanguage()
                    .collectAsState(initial = "Select Langauge")
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backGroundColor),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "DataStore", fontSize = 20.sp)
                            },
                            actions = {
                                Text(
                                    if (isDarkTheme) "Dark" else "Light",
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                                Switch(
                                    checked = isDarkTheme,
                                    onCheckedChange = {
                                        courutionScope.launch {
                                            darkPref.value.setDarkMode(it)
                                        }
                                    }
                                )
                            },

                            )
                    },
                ) { innerPadding ->
                    if (isDarkTheme) {
                        backGroundColor = Black
                        textColor = White
                    } else {
                        backGroundColor = White
                        textColor = Black
                    }
                    DataStoreRepresentation(
                        preference = preference.value, courutionScope,
                        innerPadding,
                        backGroundColor,
                        textColor,
                        preferenceLanguage.value,
                        language
                    )


                }


            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DataStoreRepresentation(
    preference: PrefUtils,
    courution: CoroutineScope,
    innnerPaddingValues: PaddingValues,
    colour: Color,
    textColor: Color,
    prefUtlisLang: PrefUtlisLang,
    langauge: String

) {


    Column(
        modifier = Modifier
            .padding(innnerPaddingValues)
            .background(colour)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }


//        var fetchKey by remember { mutableStateOf("") }

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
                courution.launch {
                    preference.saveData(key, value)
                }
            }
        ) {
            Text("Save Data")
        }

        Spacer(Modifier.padding(20.dp))

        Spacer(Modifier.padding(10.dp))

        var fetchText by remember { mutableStateOf("") }

        Text(fetchText, color = textColor)

        val listings by preference.getAllPreferences().collectAsState(initial = emptyMap())


        LazyColumn(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Gray)
                .height(100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(listings.entries.toList()) { items ->
                Text(items.value.toString(), color = textColor)
            }


        }



        Button(
            onClick = {
                courution.launch {
                    preference.deleteAllPref()
                }
            }
        ) {
            Text(text = "Clear", fontSize = 20.sp)
        }

        var dropDownMenu by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .clickable { dropDownMenu = true }

        ) {
            Text(langauge, color = textColor)
            DropdownMenu(
                expanded = dropDownMenu,
                onDismissRequest = { dropDownMenu = false },
                containerColor = Red,

                ) {
                listofData.forEach { data ->

                    DropdownMenuItem(
                        text = {
                            Text(data.name, fontSize = 20.sp)
                        },
                        onClick = {
                            courution.launch {

                                prefUtlisLang.setLanguage(data.name)
                            }
                        }
                    )
                }
            }
        }

    }
}