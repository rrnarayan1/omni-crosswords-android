package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CompactCrosswordKeyboard(
    onKeyClick: (Char) -> Unit,
    onDelete: () -> Unit
) {
    val rows = listOf(
        "QWERTYUIOP".toList(),
        "ASDFGHJKL".toList(),
        "ZXCVBNM".toList()
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp) // Extra breathability above the system bar
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    // Add extra spacer for the middle row to give it that staggered look
                    if (rowIndex == 1) Spacer(modifier = Modifier.width(12.dp))

                    // Add Blank button that doesn't do anything
                    if (rowIndex == 2) {
                        KeyButton(
                            text = "",
                            modifier = Modifier.weight(1f),
                            onClick = {  }
                        )
                    }

                    row.forEach { char ->
                        KeyButton(
                            text = char.toString(),
                            modifier = Modifier.weight(1f),
                            onClick = { onKeyClick(char) }
                        )
                    }

                    if (rowIndex == 1) Spacer(modifier = Modifier.width(12.dp))

                    // Add Delete button to the end of the bottom row
                    if (rowIndex == 2) {
                        KeyButton(
                            text = "⌫",
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            onClick = onDelete
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Spacer(modifier = Modifier.weight(1.5f))
                // Wide Space Bar (takes up 70% of the row)
                KeyButton(
                    text = "SPACE",
                    modifier = Modifier.weight(7f),
                ) { onKeyClick(' ') }
                Spacer(modifier = Modifier.weight(1.5f))
            }
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp) // Fixed small height
            .clip(RoundedCornerShape(4.dp))
            .background(containerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}