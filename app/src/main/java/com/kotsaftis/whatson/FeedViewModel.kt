package com.kotsaftis.whatson

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotsaftis.whatson.data.DEFAULT_FEED_URL
import com.kotsaftis.whatson.data.Feed
import com.kotsaftis.whatson.data.FeedRepository
import com.kotsaftis.whatson.data.FeedSource
import com.kotsaftis.whatson.data.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val feed: Feed = Feed(),
    val source: FeedSource = FeedSource.BUNDLED,
    val feedUrl: String = DEFAULT_FEED_URL,
    val loading: Boolean = true,
    val error: String? = null,
)

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = PreferencesRepository(application)
    private val repository = FeedRepository(application, preferences)

    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.feedUrlFlow.collect { url ->
                _state.update { it.copy(feedUrl = url) }
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = repository.load()
            _state.update {
                it.copy(
                    feed = result.feed,
                    source = result.source,
                    loading = false,
                    error = result.error,
                )
            }
        }
    }

    fun setFeedUrl(url: String) {
        viewModelScope.launch {
            preferences.setFeedUrl(url)
            refresh()
        }
    }

    fun resetFeedUrl() {
        setFeedUrl(DEFAULT_FEED_URL)
    }
}
