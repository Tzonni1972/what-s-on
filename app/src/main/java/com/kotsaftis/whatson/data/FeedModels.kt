package com.kotsaftis.whatson.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val week: String = "",
    val region: String = "",
    val timezone: String = "",
    val asOf: String = "",
    val lane: List<String> = emptyList(),
    val sections: FeedSections = FeedSections(),
)

@Serializable
data class FeedSections(
    val thisWeek: List<FeedItem> = emptyList(),
    val movies: List<FeedItem> = emptyList(),
    val justIn: List<FeedItem> = emptyList(),
    val pao: List<FeedItem> = emptyList(),
    val live: List<FeedItem> = emptyList(),
)

@Serializable
data class FeedItem(
    val id: String = "",
    val title: String = "",
    val kind: String = "",
    val why: String = "",
    val service: String = "",
    @SerialName("when")
    val schedule: String = "",
    val imdb: String? = null,
    val link: String? = null,
    val alreadyWatching: Boolean = false,
    val weekAhead: String? = null,
    val recap: Boolean = false,
) {
    fun imdbLabel(): String {
        val raw = imdb?.trim().orEmpty()
        return if (raw.isEmpty() || raw == "—" || raw.equals("n/a", ignoreCase = true)) {
            "—"
        } else {
            raw
        }
    }

    fun isAlreadyOnIt(): Boolean = alreadyWatching ||
        title.contains("reacher", ignoreCase = true)
}

enum class FeedSource {
    NETWORK,
    BUNDLED,
}

data class FeedLoadResult(
    val feed: Feed,
    val source: FeedSource,
    val error: String? = null,
)

data class HomeSection(
    val id: String,
    val label: String,
    val items: List<FeedItem>,
    val wide: Boolean = false,
)

fun Feed.homeSections(): List<HomeSection> = listOf(
    HomeSection("thisWeek", "THIS WEEK", sections.thisWeek),
    HomeSection("movies", "MOVIES", sections.movies),
    HomeSection("justIn", "JUST IN", sections.justIn),
    HomeSection("pao", "PANATHINAIKOS · EUROLEAGUE", sections.pao, wide = true),
).filter { it.items.isNotEmpty() }

fun Feed.regionLabel(): String {
    val first = region.substringBefore(',').trim()
    return first.ifBlank { "ATHENS" }.uppercase()
}

fun Feed.weekLabel(): String = week.ifBlank { "WEEK" }.uppercase()

fun Feed.servicesInUse(): List<String> {
    val items = sections.thisWeek + sections.movies + sections.justIn +
        sections.pao + sections.live
    return items.map { it.service.trim() }
        .filter { it.isNotEmpty() }
        .distinctBy { it.uppercase() }
}

fun Feed.laneLabel(): String = lane
    .map { it.trim() }
    .filter { it.isNotEmpty() }
    .joinToString(" · ") { it.uppercase() }
