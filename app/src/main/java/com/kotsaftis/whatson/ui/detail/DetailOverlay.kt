package com.kotsaftis.whatson.ui.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.kotsaftis.whatson.data.FeedItem
import com.kotsaftis.whatson.data.ServiceLauncher
import com.kotsaftis.whatson.ui.home.AlreadyOnItBadge
import com.kotsaftis.whatson.ui.home.GoldRedRule
import com.kotsaftis.whatson.ui.theme.Charcoal
import com.kotsaftis.whatson.ui.theme.Gold
import com.kotsaftis.whatson.ui.theme.Hairline
import com.kotsaftis.whatson.ui.theme.Muted
import com.kotsaftis.whatson.ui.theme.Paper
import com.kotsaftis.whatson.ui.theme.Playfair
import com.kotsaftis.whatson.ui.theme.Scrim
import com.kotsaftis.whatson.ui.theme.SourceSans

@Composable
fun DetailOverlay(
    item: FeedItem,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val openFocus = remember { FocusRequester() }
    BackHandler(onBack = onDismiss)
    LaunchedEffect(item.id) {
        runCatching { openFocus.requestFocus() }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Scrim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .width(560.dp)
                .background(Paper)
                .border(1.dp, Hairline, RectangleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .padding(28.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = item.title,
                            fontFamily = Playfair,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Charcoal,
                        )
                        if (item.isAlreadyOnIt()) {
                            AlreadyOnItBadge()
                        }
                    }
                    Text(
                        text = item.kind.ifBlank { "title" }.uppercase(),
                        fontFamily = SourceSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.6.sp,
                        color = Gold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.imdbLabel(),
                        fontFamily = Playfair,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 28.sp,
                        color = Gold,
                    )
                    Text(
                        text = "IMDb",
                        fontFamily = SourceSans,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = Muted,
                    )
                }
            }
            GoldRedRule(Modifier.padding(vertical = 16.dp))
            MetaLine(label = "WHY", value = item.why.ifBlank { "—" })
            MetaLine(label = "SERVICE", value = item.service.ifBlank { "—" }.uppercase())
            MetaLine(label = "WHEN", value = item.schedule.ifBlank { "—" })
            if (!item.weekAhead.isNullOrBlank()) {
                MetaLine(label = "NOTE", value = item.weekAhead)
            }
            if (item.recap) {
                MetaLine(label = "FLAG", value = "RECAP")
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FocusButton(
                    label = if (item.link.isNullOrBlank()) "NO LINK" else "OPEN LINK",
                    enabled = !item.link.isNullOrBlank(),
                    modifier = Modifier.focusRequester(openFocus),
                    onClick = { ServiceLauncher.open(context, item) },
                )
                FocusButton(
                    label = "BACK",
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun MetaLine(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            fontFamily = SourceSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = Gold,
            modifier = Modifier.width(96.dp),
        )
        Text(
            text = value,
            fontFamily = SourceSans,
            fontSize = 16.sp,
            color = Charcoal,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun FocusButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val border = when {
        focused -> Gold
        else -> Hairline
    }
    Text(
        text = label,
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 1.4.sp,
        color = if (enabled) Charcoal else Muted,
        modifier = modifier
            .border(if (focused) 3.dp else 1.dp, border, RectangleShape)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
    )
}
