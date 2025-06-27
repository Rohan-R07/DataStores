package com.example.datastores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PrefViewModel(val contex: Context) : ViewModel() {
    //
//
    companion object {
        val languageKey = stringPreferencesKey("LANGUAGE")
        val darkModeKey = booleanPreferencesKey("DARK_MODE")
//
    }

    //
    val Context.dataStore by preferencesDataStore(name = "local")

    //
    suspend fun saveData(key: String, value: String) {
        contex.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    //
    suspend fun getData(key: String): String? {
        return contex.dataStore.data.map {
            it[stringPreferencesKey(key)]
        }.first()
    }

    //
    fun getAllPreferences(): Flow<Map<String, Any?>> {
        return contex.dataStore.data
            .map {
                it.asMap().mapKeys { it.key.name }
                    .mapValues { it.value.toString() }
            }
    }

    //
    // can be used if needed
    suspend fun deleteAllPref() {
        contex.dataStore.edit {
            it.clear()
        }
    }

    //
//
    //// DARK MODE
//
    val Context.darkModePref by preferencesDataStore("darkMode")

    //
//
    suspend fun setDarkMode(value: Boolean) {
        contex.darkModePref.edit {
            it[darkModeKey] = value
        }
    }

    //
    fun readDarkMode(): Flow<Boolean> {
        return contex.darkModePref.data.map {
            it[darkModeKey] ?: false
        }
    }

    //
    // LANGUAGE
//
    val Context.darkModePreflang by preferencesDataStore("language")

    //
//
    suspend fun setLanguage(value: String) {
        contex.darkModePreflang.edit {
            it[languageKey] = value
        }
    }

    //
    fun readLanguage(): Flow<String> {
        return contex.darkModePreflang.data.map {
            it[languageKey] ?: "Select Language"
        }
    }
}
//
//
