package com.kotsaftis.whatson.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Typography
import androidx.tv.material3.lightColorScheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val WhatsOnScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Paper,
    secondary = RuleRed,
    onSecondary = Paper,
    background = Paper,
    onBackground = Charcoal,
    surface = Paper,
    onSurface = Charcoal,
    border = Hairline,
)

private val WhatsOnTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Playfair,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        color = Charcoal,
    ),
    headlineMedium = TextStyle(
        fontFamily = Playfair,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Charcoal,
    ),
    titleMedium = TextStyle(
        fontFamily = Playfair,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 28.sp,
        color = Gold,
    ),
    bodyMedium = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = Charcoal,
    ),
    labelSmall = TextStyle(
        fontFamily = SourceSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 1.4.sp,
        color = Gold,
    ),
)

@Composable
fun WhatsOnTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WhatsOnScheme,
        typography = WhatsOnTypography,
    ) {
        CompositionLocalProvider(LocalContentColor provides Charcoal) {
            content()
        }
    }
}

fun Color.faint(alpha: Float): Color = copy(alpha = alpha)
