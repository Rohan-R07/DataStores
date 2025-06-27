package com.example.datastores

import android.content.Context
import android.preference.PreferenceDataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PrefUtils(val contex: Context) {

    val Context.dataStore by preferencesDataStore(name = "local")

    suspend fun saveData(key: String, value: String) {
        contex.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getData(key: String): String? {
        return contex.dataStore.data.map {
            it[stringPreferencesKey(key)]
        }.first()
    }

    //    val listings = contex.dataStore.data
    fun getAllPreferences(): Flow<Map<String, Any?>> {
        return contex.dataStore.data
            .map {
                it.asMap().mapKeys { it.key.name }
                    .mapValues { it.value.toString() }
            }
    }

    suspend fun deleteAllPref() {
        contex.dataStore.edit {
            it.clear()
        }
    }
}


class PrefUtilsDark(val contex: Context) {

    val Context.darkModePref by preferencesDataStore("darkMode")

    //Dark Mode
    companion object {
        val darkModeKey = booleanPreferencesKey("DARK_MODE")
    }

    suspend fun setDarkMode(value: Boolean) {
        contex.darkModePref.edit {
            it[darkModeKey] = value
        }
    }

    fun readDarkMode(): Flow<Boolean> {
        return contex.darkModePref.data.map {
            it[darkModeKey] ?: false
        }
    }

}


class PrefUtlisLang(val contex: Context) {

    val Context.darkModePref by preferencesDataStore("language")

    //Dark Mode
    companion object {
        val languageKey = stringPreferencesKey("LANGUAGE")
    }

    suspend fun setLanguage(value: String) {
        contex.darkModePref.edit {
            it[languageKey] = value
        }
    }

    fun readLanguage(): Flow<String> {
        return contex.darkModePref.data.map {
            it[languageKey] ?: "Select Language"
        }
    }

}