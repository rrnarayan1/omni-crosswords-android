package com.rohanNarayan.omnicrosswords.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohanNarayan.omnicrosswords.data.Crossword
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CrosswordListItemView(navController: NavController, crossword: Crossword) {
    val outletName = crossword.outletName
    val date = crossword.date
    val formattedDate = DateTimeFormatter.ofPattern("EE M/d/yy")
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochSecond(date))
    val title = "$outletName - $formattedDate"

    Row(modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .fillMaxWidth()
        .clickable {navController.navigate(route = Screen.Crossword.route + "?crosswordId=${crossword.id}")},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)

        val fillableSquares: Int = crossword.symbols.filter { it != -1 }.size
        val filledSquares: Int = crossword.entry.filter { it != "." }.filter { it.isNotEmpty() }.size
        val progress: Float = filledSquares.toFloat() / fillableSquares

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (crossword.isSolved) {
                Icon(imageVector = Icons.Default.CheckCircle,
                    tint = Color(0x9934C759),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Solved")
            } else if (progress > 0) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFFFF9500),
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color.Gray,
            )
        }
    }
}

