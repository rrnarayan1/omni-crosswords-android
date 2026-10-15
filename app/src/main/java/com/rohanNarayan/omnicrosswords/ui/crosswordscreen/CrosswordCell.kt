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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohanNarayan.omnicrosswords.ui.theme.BlueFocused25
import com.rohanNarayan.omnicrosswords.ui.theme.BlueFocused50
import com.rohanNarayan.omnicrosswords.ui.theme.BlueFocused60
import com.rohanNarayan.omnicrosswords.ui.theme.BlueFocused80
import com.rohanNarayan.omnicrosswords.ui.theme.GreyBackground
import com.rohanNarayan.omnicrosswords.ui.theme.InProgressOrange
import com.rohanNarayan.omnicrosswords.ui.theme.RedError40
import com.rohanNarayan.omnicrosswords.ui.theme.RedError50
import com.rohanNarayan.omnicrosswords.ui.theme.RedError70
import com.rohanNarayan.omnicrosswords.ui.theme.ShadedGrey

@Composable
fun CrosswordCell(
    value: String,
    correctValue: String,
    errorTrackingEnabled: Boolean,
    showHelpReceived: Boolean,
    symbol: Int,
    isFocusedTag: Boolean,
    isHighlighted: Boolean,
    boxWidth: Float,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val isEditable = symbol != -1
    val isShaded = symbol >= 10000
    val isCellRed = errorTrackingEnabled && value != "" && value != correctValue
    val backgroundColor = when {
        !isEditable -> Color.Black
        isCellRed && isFocusedTag -> RedError70
        isCellRed && isHighlighted -> RedError50
        isCellRed -> RedError40
        isDarkMode && isFocusedTag -> BlueFocused80
        isDarkMode && isHighlighted -> BlueFocused50
        !isDarkMode && isFocusedTag -> BlueFocused60
        !isDarkMode && isHighlighted -> BlueFocused25
        isShaded -> ShadedGrey
        isDarkMode -> GreyBackground
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

            if (showHelpReceived) {
                Canvas(modifier = Modifier.size(boxWidth.dp)) {
                    val path = Path().apply {
                        moveTo(4 * size.width / 5f, size.height) // mid-bottom
                        lineTo(size.width, 4 * size.height / 5f) // mid-right
                        lineTo(size.width, size.height)      // bottom right
                        close()                              // Connects back to top point
                    }
                    drawPath(
                        path = path,
                        color = InProgressOrange,
                        style = Fill
                    )
                }
            }

            if (symbol % 1000 != 0) {
                // clue number
                val clueNum = symbol % 1000
                Text(
                    text = clueNum.toString(),
                    style = TextStyle(fontSize = (boxWidth * 0.2).sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                )
            }
        }
    }
}
