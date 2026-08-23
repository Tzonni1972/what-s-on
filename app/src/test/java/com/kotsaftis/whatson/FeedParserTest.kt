package com.kotsaftis.whatson

import com.kotsaftis.whatson.data.FeedParser
import com.kotsaftis.whatson.data.homeSections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedParserTest {
    @Test
    fun parsesBundledWeekAndIgnoresUnknownFields() {
        val feed = FeedParser.parse(SAMPLE)
        assertEquals("24–30 Aug 2026", feed.week)
        assertEquals("Athens, Greece", feed.region)
        assertEquals("Europe/Athens", feed.timezone)
        assertEquals("2026-08-22", feed.asOf)
        assertEquals(7, feed.lane.size)
        assertEquals(4, feed.sections.thisWeek.size)
        assertEquals(4, feed.sections.movies.size)
        assertEquals(4, feed.sections.justIn.size)
        assertEquals(1, feed.sections.pao.size)
        assertTrue(feed.sections.live.isEmpty())

        val reacher = feed.sections.thisWeek.first { it.id == "reacher-s4" }
        assertTrue(reacher.alreadyWatching)
        assertTrue(reacher.isAlreadyOnIt())
        assertEquals("8.0", reacher.imdbLabel())

        val lanterns = feed.sections.thisWeek.first { it.id == "lanterns" }
        assertEquals("—", lanterns.imdbLabel())

        val titles = (
            feed.sections.thisWeek +
                feed.sections.movies +
                feed.sections.justIn +
                feed.sections.pao
            ).map { it.title.lowercase() }
        assertFalse(titles.any { it.contains("flex") || it.contains("scream") })

        val labels = feed.homeSections().map { it.label }
        assertEquals(
            listOf("THIS WEEK", "MOVIES", "JUST IN", "PANATHINAIKOS · EUROLEAGUE"),
            labels,
        )
    }

    @Test
    fun missingImdbBecomesDash() {
        val feed = FeedParser.parse(
            """
            {"sections":{"thisWeek":[{"id":"x","title":"X"}]}}
            """.trimIndent(),
        )
        assertEquals("—", feed.sections.thisWeek.first().imdbLabel())
    }

    companion object {
        private val SAMPLE = """
            {
              "week": "24–30 Aug 2026",
              "region": "Athens, Greece",
              "timezone": "Europe/Athens",
              "asOf": "2026-08-22",
              "unknownFutureField": true,
              "lane": ["thriller","crime","cop","mafia","Nordic noir","military","sci-fi"],
              "sections": {
                "thisWeek": [
                  {"id":"lioness-s3","title":"Lioness S3","imdb":"7.8"},
                  {"id":"reacher-s4","title":"Reacher S4","alreadyWatching":true,"imdb":"8.0"},
                  {"id":"ludwig-s2","title":"Ludwig S2","imdb":"8.1"},
                  {"id":"lanterns","title":"Lanterns","imdb":"—"}
                ],
                "movies": [
                  {"id":"the-whisper-man","title":"The Whisper Man"},
                  {"id":"the-secret-woman","title":"The Secret Woman"},
                  {"id":"facing-el-chapo","title":"Facing El Chapo","recap":true},
                  {"id":"no-other-choice","title":"No Other Choice","imdb":"7.6"}
                ],
                "justIn": [
                  {"id":"blood-sacrifice","title":"Blood Sacrifice"},
                  {"id":"echo-3","title":"Echo 3","imdb":"6.5"},
                  {"id":"seal-team","title":"SEAL Team"},
                  {"id":"umthetho","title":"Umthetho"}
                ],
                "pao": [
                  {"id":"pao-vs-paris-2026-09-24","title":"PAO vs Paris","kind":"sport"}
                ],
                "live": []
              }
            }
        """.trimIndent()
    }
}
