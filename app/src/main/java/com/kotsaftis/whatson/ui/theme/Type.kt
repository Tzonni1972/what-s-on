package com.kotsaftis.whatson.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.kotsaftis.whatson.R

val Playfair = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.playfair_display_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.playfair_display_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.playfair_display_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

val SourceSans = FontFamily(
    Font(R.font.source_sans_3_regular, FontWeight.Normal),
    Font(R.font.source_sans_3_semibold, FontWeight.SemiBold),
    Font(R.font.source_sans_3_bold, FontWeight.Bold),
)
