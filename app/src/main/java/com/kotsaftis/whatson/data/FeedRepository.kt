package com.kotsaftis.whatson.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class FeedRepository(
    private val context: Context,
    private val preferences: PreferencesRepository,
    private val client: OkHttpClient = defaultClient,
) {
    suspend fun load(): FeedLoadResult = withContext(Dispatchers.IO) {
        val url = preferences.feedUrl()
        val remoteError = runCatching {
            fetchRemote(url)?.let { raw ->
                return@withContext FeedLoadResult(
                    feed = FeedParser.parse(raw),
                    source = FeedSource.NETWORK,
                )
            }
            "Empty response"
        }.exceptionOrNull()?.message

        FeedLoadResult(
            feed = loadBundled(),
            source = FeedSource.BUNDLED,
            error = remoteError ?: "Remote feed unavailable",
        )
    }

    private fun fetchRemote(url: String): String? {
        if (url.isBlank()) return null
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("HTTP ${response.code}")
            }
            return response.body?.string()?.takeIf { it.isNotBlank() }
        }
    }

    private fun loadBundled(): Feed {
        val raw = context.assets.open(BUNDLED_FEED_ASSET).bufferedReader().use { it.readText() }
        return FeedParser.parse(raw)
    }

    companion object {
        const val BUNDLED_FEED_ASSET = "feed.json"

        private val defaultClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .callTimeout(12, TimeUnit.SECONDS)
            .build()
    }
}
