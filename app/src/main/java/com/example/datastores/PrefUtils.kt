package com.example.datastores

import android.content.Context
import android.preference.PreferenceDataStore
import androidx.datastore.dataStore
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
    //Dark Mode


}