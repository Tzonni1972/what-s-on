package com.kotsaftis.whatson.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "whatson_settings")

class PreferencesRepository(private val context: Context) {
    private val feedUrlKey = stringPreferencesKey("feed_url")

    val feedUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[feedUrlKey] ?: DEFAULT_FEED_URL
    }

    suspend fun feedUrl(): String = feedUrlFlow.first()

    suspend fun setFeedUrl(url: String) {
        val cleaned = url.trim().ifBlank { DEFAULT_FEED_URL }
        context.dataStore.edit { it[feedUrlKey] = cleaned }
    }
}

const val DEFAULT_FEED_URL =
    "https://raw.githubusercontent.com/Tzonni1972/what-s-on/main/feed.json"
