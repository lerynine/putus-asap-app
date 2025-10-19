package com.example.putusasap

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

val Context.rokokDataStore by preferencesDataStore("rokok_prefs") // ✅ nama unik

object RokokPreferences {
    private val KONSUMSI_KEY = intPreferencesKey("konsumsi_hari_ini")
    private val TANGGAL_KEY = stringPreferencesKey("tanggal_terakhir")

    suspend fun saveKonsumsi(context: Context, jumlah: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        context.rokokDataStore.edit { prefs ->
            prefs[KONSUMSI_KEY] = jumlah
            prefs[TANGGAL_KEY] = today
        }
    }

    suspend fun loadKonsumsi(context: Context): Int {
        val prefs = context.rokokDataStore.data.first()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val lastDate = prefs[TANGGAL_KEY]
        return if (lastDate == today) {
            prefs[KONSUMSI_KEY] ?: 0
        } else {
            0 // reset jika sudah hari baru
        }
    }
}
