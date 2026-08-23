package com.kotsaftis.whatson.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.kotsaftis.whatson.R
import com.kotsaftis.whatson.ui.theme.Paper
import com.kotsaftis.whatson.ui.theme.Stone

@Composable
fun AthensBackdrop(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.paper_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Paper.copy(alpha = 0.72f))
            val w = size.width
            val h = size.height
            val originX = w * 0.62f
            val originY = h * 0.02f
            val monumentW = w * 0.36f
            val monumentH = h * 0.42f
            drawParthenon(
                origin = Offset(originX, originY),
                size = Size(monumentW, monumentH),
                color = Stone,
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawParthenon(
    origin: Offset,
    size: Size,
    color: Color,
) {
    val columns = 8
    val pedimentH = size.height * 0.22f
    val entablatureH = size.height * 0.10f
    val stylobateH = size.height * 0.08f
    val colTop = origin.y + pedimentH
    val colBottom = origin.y + size.height - stylobateH
    val colH = colBottom - colTop - entablatureH
    val colW = size.width / (columns * 2.15f)
    val gap = (size.width - colW * columns) / (columns + 1)

    val pediment = Path().apply {
        moveTo(origin.x + size.width / 2f, origin.y)
        lineTo(origin.x + size.width + gap, colTop)
        lineTo(origin.x - gap, colTop)
        close()
    }
    drawPath(pediment, color = color, style = Stroke(width = 3f, cap = StrokeCap.Round))
    drawRect(
        color = color,
        topLeft = Offset(origin.x - gap * 0.3f, colTop),
        size = Size(size.width + gap * 0.6f, entablatureH),
    )
    repeat(columns) { i ->
        val x = origin.x + gap + i * (colW + gap)
        drawRect(
            color = color,
            topLeft = Offset(x, colTop + entablatureH),
            size = Size(colW, colH),
        )
    }
    drawRect(
        color = color,
        topLeft = Offset(origin.x - gap * 0.4f, colBottom),
        size = Size(size.width + gap * 0.8f, stylobateH),
    )
}
