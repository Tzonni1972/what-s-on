package com.kotsaftis.whatson.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.kotsaftis.whatson.data.Feed
import com.kotsaftis.whatson.data.FeedItem
import com.kotsaftis.whatson.data.homeSections
import com.kotsaftis.whatson.data.laneLabel
import com.kotsaftis.whatson.data.regionLabel
import com.kotsaftis.whatson.data.servicesInUse
import com.kotsaftis.whatson.data.weekLabel
import com.kotsaftis.whatson.ui.theme.Charcoal
import com.kotsaftis.whatson.ui.theme.Gold
import com.kotsaftis.whatson.ui.theme.Muted
import com.kotsaftis.whatson.ui.theme.Playfair
import com.kotsaftis.whatson.ui.theme.RuleRed
import com.kotsaftis.whatson.ui.theme.SourceSans

@Composable
fun HomeScreen(
    feed: Feed,
    onOpenItem: (FeedItem) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstFocus = remember { FocusRequester() }
    val sections = feed.homeSections()
    LaunchedEffect(sections.firstOrNull()?.items?.firstOrNull()?.id) {
        kotlinx.coroutines.delay(80)
        runCatching { firstFocus.requestFocus() }
    }

    Box(modifier.fillMaxSize()) {
        AthensBackdrop()
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 22.dp),
        ) {
            Header(
                feed = feed,
                onOpenSettings = onOpenSettings,
            )
            GoldRedRule(Modifier.padding(top = 10.dp, bottom = 10.dp))
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                sections.forEachIndexed { sectionIndex, section ->
                    Column {
                        Text(
                            text = section.label,
                            fontFamily = SourceSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.6.sp,
                            color = Gold,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 8.dp),
                        ) {
                            items(section.items, key = { it.id.ifBlank { it.title } }) { item ->
                                val cardModifier = if (sectionIndex == 0 && item == section.items.first()) {
                                    Modifier.focusRequester(firstFocus)
                                } else {
                                    Modifier
                                }
                                TitleCard(
                                    item = item,
                                    onClick = { onOpenItem(item) },
                                    wide = section.wide,
                                    modifier = cardModifier,
                                )
                            }
                        }
                    }
                }
            }
            Footer(feed)
        }
    }
}

@Composable
private fun Header(
    feed: Feed,
    onOpenSettings: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "What's ",
                fontFamily = Playfair,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = Charcoal,
            )
            Text(
                text = "on",
                fontFamily = Playfair,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                fontSize = 30.sp,
                color = Gold,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = feed.regionLabel(),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                color = Charcoal,
            )
            Text(
                text = feed.weekLabel(),
                fontFamily = SourceSans,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.2.sp,
                color = Charcoal,
                modifier = Modifier.padding(top = 2.dp),
            )
            SettingsLink(onOpenSettings)
        }
    }
}

@Composable
private fun SettingsLink(onOpenSettings: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Text(
        text = "SETTINGS",
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 1.4.sp,
        color = if (focused) Gold else Muted,
        modifier = Modifier
            .padding(top = 6.dp)
            .border(
                width = if (focused) 2.dp else 0.dp,
                color = if (focused) Gold else androidx.compose.ui.graphics.Color.Transparent,
                shape = RectangleShape,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onOpenSettings,
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
fun GoldRedRule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(Brush.horizontalGradient(listOf(Gold, RuleRed))),
    )
}

@Composable
private fun Footer(feed: Feed) {
    val lane = feed.laneLabel().ifBlank { "THRILLER · CRIME · COP · MAFIA · MILITARY" }
    val services = feed.servicesInUse().joinToString(" · ") { it.uppercase() }
        .ifBlank { "NETFLIX · PRIME · MAX · COSMOTE · DISNEY+ · APPLE TV" }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(RuleRed),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = lane,
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.1.sp,
                color = Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(520.dp),
            )
        }
        Text(
            text = services,
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            letterSpacing = 1.1.sp,
            color = Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
