package com.kotsaftis.whatson.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.kotsaftis.whatson.data.FeedItem
import com.kotsaftis.whatson.ui.theme.BadgeRed
import com.kotsaftis.whatson.ui.theme.Charcoal
import com.kotsaftis.whatson.ui.theme.Gold
import com.kotsaftis.whatson.ui.theme.Hairline
import com.kotsaftis.whatson.ui.theme.Paper
import com.kotsaftis.whatson.ui.theme.Playfair
import com.kotsaftis.whatson.ui.theme.SourceSans

@Composable
fun TitleCard(
    item: FeedItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 214.dp,
    height: Dp = 102.dp,
    wide: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor = if (focused) Gold else Hairline
    val borderWidth = if (focused) 3.dp else 1.dp

    Box(
        modifier
            .width(if (wide) widthInWide else width)
            .height(height)
            .border(borderWidth, borderColor, RectangleShape)
            .background(Paper)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        if (wide) {
            WideCardBody(item)
        } else {
            StandardCardBody(item)
        }
    }
}

private val widthInWide = 536.dp

@Composable
private fun StandardCardBody(item: FeedItem) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.title,
                    fontFamily = Playfair,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Charcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (item.isAlreadyOnIt()) {
                    AlreadyOnItBadge()
                }
            }
            Text(
                text = item.imdbLabel(),
                fontFamily = Playfair,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Gold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Text(
            text = item.why.ifBlank { item.weekAhead.orEmpty() },
            fontFamily = SourceSans,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color = Charcoal,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = item.schedule,
                fontFamily = SourceSans,
                fontSize = 11.sp,
                color = Charcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = item.service.uppercase(),
                fontFamily = SourceSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.1.sp,
                color = Gold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun WideCardBody(item: FeedItem) {
    val (day, time) = splitSchedule(item.schedule)
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontFamily = Playfair,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Charcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.why.ifBlank { item.weekAhead.orEmpty() },
                fontFamily = SourceSans,
                fontSize = 13.sp,
                color = Charcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = day,
                fontFamily = Playfair,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Charcoal,
            )
            Text(
                text = time,
                fontFamily = Playfair,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 22.sp,
                color = Gold,
            )
        }
        Text(
            text = item.service.uppercase(),
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 1.2.sp,
            color = Gold,
        )
    }
}

@Composable
fun AlreadyOnItBadge() {
    Box(
        Modifier
            .background(BadgeRed)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = "ALREADY ON IT",
            fontFamily = SourceSans,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            letterSpacing = 0.6.sp,
            color = Paper,
        )
    }
}

private fun splitSchedule(schedule: String): Pair<String, String> {
    val parts = schedule.split("·").map { it.trim() }.filter { it.isNotEmpty() }
    return if (parts.size >= 2) {
        parts[0] to parts[1]
    } else {
        schedule to ""
    }
}
