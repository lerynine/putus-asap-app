package com.example.putusasap

import android.content.Context
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore by preferencesDataStore("air_prefs")

class UserPreferences(private val context: Context) {
    private val WATER_KEY = floatPreferencesKey("water")
    private val DATE_KEY = stringPreferencesKey("last_date")

    suspend fun saveWater(value: Float) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.dataStore.edit { prefs ->
            prefs[WATER_KEY] = value
            prefs[DATE_KEY] = today
        }
    }

    fun getWater(): Flow<Float> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return context.dataStore.data.map { prefs ->
            val savedDate = prefs[DATE_KEY]
            if (savedDate != today) {
                // Hari sudah ganti → reset
                0f
            } else {
                prefs[WATER_KEY] ?: 0f
            }
        }
    }
}
