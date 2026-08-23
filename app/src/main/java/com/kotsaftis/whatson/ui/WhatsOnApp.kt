package com.kotsaftis.whatson.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotsaftis.whatson.FeedViewModel
import com.kotsaftis.whatson.data.FeedItem
import com.kotsaftis.whatson.ui.detail.DetailOverlay
import com.kotsaftis.whatson.ui.home.HomeScreen
import com.kotsaftis.whatson.ui.settings.SettingsScreen
import com.kotsaftis.whatson.ui.theme.WhatsOnTheme

private enum class Page { Home, Settings }

@Composable
fun WhatsOnApp(viewModel: FeedViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var page by remember { mutableStateOf(Page.Home) }
    var selected by remember { mutableStateOf<FeedItem?>(null) }

    WhatsOnTheme {
        Box(
            Modifier
                .fillMaxSize()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Menu) {
                        page = Page.Settings
                        selected = null
                        true
                    } else {
                        false
                    }
                },
        ) {
            when (page) {
                Page.Home -> HomeScreen(
                    feed = state.feed,
                    onOpenItem = { selected = it },
                    onOpenSettings = { page = Page.Settings },
                )
                Page.Settings -> SettingsScreen(
                    state = state,
                    onSaveUrl = viewModel::setFeedUrl,
                    onRefresh = viewModel::refresh,
                    onReset = viewModel::resetFeedUrl,
                    onBack = { page = Page.Home },
                )
            }
            val item = selected
            if (page == Page.Home && item != null) {
                DetailOverlay(
                    item = item,
                    onDismiss = { selected = null },
                )
            }
        }
    }
}
