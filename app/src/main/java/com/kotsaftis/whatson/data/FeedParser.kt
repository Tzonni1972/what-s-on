package com.kotsaftis.whatson.data

import kotlinx.serialization.json.Json

object FeedParser {
    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun parse(raw: String): Feed = json.decodeFromString(Feed.serializer(), raw)
}
