package com.kotsaftis.whatson.ui.settings

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import androidx.tv.material3.Text
import com.kotsaftis.whatson.FeedUiState
import com.kotsaftis.whatson.data.DEFAULT_FEED_URL
import com.kotsaftis.whatson.data.FeedSource
import com.kotsaftis.whatson.ui.detail.FocusButton
import com.kotsaftis.whatson.ui.home.AthensBackdrop
import com.kotsaftis.whatson.ui.home.GoldRedRule
import com.kotsaftis.whatson.ui.theme.Charcoal
import com.kotsaftis.whatson.ui.theme.Gold
import com.kotsaftis.whatson.ui.theme.Hairline
import com.kotsaftis.whatson.ui.theme.Muted
import com.kotsaftis.whatson.ui.theme.Paper
import com.kotsaftis.whatson.ui.theme.Playfair
import com.kotsaftis.whatson.ui.theme.SourceSans

@Composable
fun SettingsScreen(
    state: FeedUiState,
    onSaveUrl: (String) -> Unit,
    onRefresh: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    var draft by remember(state.feedUrl) { mutableStateOf(state.feedUrl) }
    val refreshFocus = remember { FocusRequester() }
    BackHandler(onBack = onBack)
    LaunchedEffect(Unit) {
        runCatching { refreshFocus.requestFocus() }
    }

    Box(Modifier.fillMaxSize()) {
        AthensBackdrop()
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 36.dp),
        ) {
            Text(
                text = "Settings",
                fontFamily = Playfair,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Charcoal,
            )
            Text(
                text = "Feed URL and refresh. No accounts.",
                fontFamily = Playfair,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                color = Gold,
                modifier = Modifier.padding(top = 4.dp),
            )
            GoldRedRule(Modifier.padding(vertical = 16.dp))
            Text(
                text = "FEED URL",
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.6.sp,
                color = Gold,
            )
            Box(
                Modifier
                    .padding(top = 8.dp, bottom = 18.dp)
                    .fillMaxWidth()
                    .border(1.dp, Hairline, RectangleShape)
                    .background(Paper)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                AndroidView(
                    factory = { context ->
                        EditText(context).apply {
                            setText(draft)
                            setTextColor(0xFF1C1814.toInt())
                            setHintTextColor(0xFF6B645C.toInt())
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            textSize = 16f
                            isSingleLine = true
                            imeOptions = EditorInfo.IME_ACTION_DONE
                            setOnEditorActionListener { _, _, _ ->
                                onSaveUrl(text.toString())
                                true
                            }
                            doAfterTextChanged { editable ->
                                draft = editable?.toString().orEmpty()
                            }
                        }
                    },
                    update = { view ->
                        if (view.text.toString() != draft && view.hasFocus().not()) {
                            view.setText(draft)
                            view.setSelection(draft.length)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Text(
                text = statusLine(state),
                fontFamily = SourceSans,
                fontSize = 14.sp,
                color = Charcoal,
            )
            if (!state.error.isNullOrBlank() && state.source == FeedSource.BUNDLED) {
                Text(
                    text = "Remote missed · ${state.error}",
                    fontFamily = SourceSans,
                    fontSize = 13.sp,
                    color = Muted,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            Text(
                text = "Default: $DEFAULT_FEED_URL",
                fontFamily = SourceSans,
                fontSize = 12.sp,
                color = Muted,
                modifier = Modifier.padding(top = 10.dp),
            )
            Row(
                Modifier.padding(top = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FocusButton(
                    label = if (state.loading) "LOADING…" else "REFRESH",
                    enabled = !state.loading,
                    modifier = Modifier.focusRequester(refreshFocus),
                    onClick = {
                        onSaveUrl(draft)
                    },
                )
                FocusButton(
                    label = "SAVE URL",
                    onClick = { onSaveUrl(draft) },
                )
                FocusButton(
                    label = "DEFAULT URL",
                    onClick = onReset,
                )
                FocusButton(
                    label = "BACK",
                    onClick = onBack,
                )
            }
        }
    }
}

private fun statusLine(state: FeedUiState): String {
    val source = when (state.source) {
        FeedSource.NETWORK -> "Loaded from network"
        FeedSource.BUNDLED -> "Bundled fallback"
    }
    val asOf = state.feed.asOf.takeIf { it.isNotBlank() }?.let { " · as of $it" }.orEmpty()
    val week = state.feed.week.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()
    return source + asOf + week
}
