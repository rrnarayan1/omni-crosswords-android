package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CrosswordCell(
    value: String,
    correctValue: String,
    errorTrackingEnabled: Boolean,
    symbol: Int,
    isFocusedTag: Boolean,
    isHighlighted: Boolean,
    boxWidth: Float,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val isEditable = symbol != -1
    val isCellRed = errorTrackingEnabled && value != "" && value != correctValue
    val backgroundColor = when {
        !isEditable -> Color.Black
        isCellRed && isFocusedTag -> Color(0xBAFF3B30)
        isCellRed && isHighlighted -> Color(0x80FF3B30)
        isCellRed -> Color(0x64FF3B30)
        isDarkMode && isFocusedTag -> Color(0xCA0A84FF)
        isDarkMode && isHighlighted -> Color(0x800A84FF)
        !isDarkMode && isFocusedTag -> Color(0x960A84FF)
        !isDarkMode && isHighlighted -> Color(0x400A84FF)
        isDarkMode -> Color(0xFF636366)
        else -> MaterialTheme.colorScheme.surface // white or grey
    }

    Box(
        modifier = Modifier
            .size(boxWidth.dp)
            .border(0.5.dp, Color.Black)
            .background(backgroundColor)
            .clickable(enabled = isEditable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isEditable) {
            val fontSize = if (value.length == 1) boxWidth * 0.8 else (boxWidth / value.length)
            Text(
                text = value,
                style = TextStyle(fontSize = fontSize.toInt().sp)
            )

            if (symbol in 1000..<10000) {
                // 1000 means cell should be circled,
                // 10000 means cell should be shaded
                Canvas(modifier = Modifier.size(boxWidth.dp)) {
                    drawCircle(
                        color = Color.Black,
                        style = Stroke(width = 1f),
                        radius = boxWidth,
                        center = center
                    )
                }
            }

            if (symbol % 1000 != 0) {
                val clueNum = symbol % 1000
                Text(
                    text = clueNum.toString(),
                    style = TextStyle(fontSize = (boxWidth*0.2).sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                )
            }
        }
    }
}