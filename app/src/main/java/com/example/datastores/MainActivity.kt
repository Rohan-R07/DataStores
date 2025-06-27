package com.example.datastores

import android.content.Context
import android.hardware.camera2.params.BlackLevelPattern
import android.os.Build
import android.os.Bundle
import android.view.RoundedCorner
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.example.datastores.ui.theme.DataStoresTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datastores.ui.theme.Purple80
import kotlinx.coroutines.CoroutineScope


class MainActivity : ComponentActivity() {

    val viewModel by viewModels<PrefViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PrefViewModel(applicationContext) as T
                }
            }
        }
    )
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoresTheme {
                val isDarkTheme by viewModel.readDarkMode().collectAsState(initial = false)
                var backGroundColor by remember {
                    mutableStateOf(White)
                }
                var textColor by remember { mutableStateOf(Black) }
                val courutionScope = rememberCoroutineScope()

                val language by viewModel.readLanguage()
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
                                            viewModel.setDarkMode(it)
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
                        preferences = viewModel,
                        courution = courutionScope,
                        innnerPaddingValues = innerPadding,
                        colour = backGroundColor,
                        textColor = textColor,
                        langauge =language
                    )


                }


            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DataStoreRepresentation(
    preferences: PrefViewModel,
    courution: CoroutineScope,
    innnerPaddingValues: PaddingValues,
    colour: Color,
    textColor: Color,
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
                    preferences.saveData(key, value)
                }
            },
            modifier = Modifier
                .padding(top = 30.dp)
        ) {
            Text("Save Data")
        }

        Spacer(Modifier.padding(20.dp))

        var fetchText by remember { mutableStateOf("") }

        Text(fetchText, color = textColor)

        val listings by preferences.getAllPreferences().collectAsState(initial = emptyMap())

        Text(
            text = "Data List",
            fontSize = 20.sp,
            color = textColor
        )

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
                Row {

                    Text("KEY: ${items.key.toString()}", color = textColor)
                    Spacer(Modifier.padding(start = 10.dp))
                    Text("VALUE: ${items.value.toString()}", color = textColor)
                }
            }
        }



        Button(
            onClick = {
                courution.launch {
                    preferences.deleteAllPref()
                }
            }
        ) {
            Text(text = "Clear", fontSize = 20.sp)
        }

        var dropDownMenu by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .height(40.dp)
                .width(200.dp)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { dropDownMenu = true }
                .background(Purple80),
            contentAlignment = Alignment.Center

        ) {
            Row {

                Text(langauge, color = Black)
                Spacer(Modifier.padding(start = 10.dp))
                Icon(
                    imageVector = if (dropDownMenu) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Black
                )
            }

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
                                preferences.setLanguage(data.name)
                                dropDownMenu = false
                            }
                        }
                    )
                }
            }
        }

    }
}