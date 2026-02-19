package com.rohanNarayan.omnicrosswords.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.ui.theme.InProgressOrange
import com.rohanNarayan.omnicrosswords.ui.theme.SuccessGreen
import com.rohanNarayan.omnicrosswords.ui.utils.getProgress
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.toFormattedDate
import com.rohanNarayan.omnicrosswords.ui.utils.verticalPadding

@Composable
fun CrosswordListItemView(navController: NavController, crossword: Crossword) {
    val outletName = crossword.outletName
    val formattedDate = toFormattedDate(crossword.date)
    val title = "$outletName - $formattedDate"

    Row(modifier = Modifier
        .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        .fillMaxWidth()
        .clickable {navController.navigate(route = Screen.Crossword.route + "?crosswordId=${crossword.id}")},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        val progress = getProgress(symbols = crossword.symbols, entry = crossword.entry)

        Row(verticalAlignment = Alignment.CenterVertically) {
            val iconSize = 20.dp
            if (crossword.isSolved) {
                Icon(imageVector = Icons.Default.CheckCircle,
                    tint = SuccessGreen,
                    modifier = Modifier.size(iconSize),
                    contentDescription = "Solved")
            } else if (progress > 0) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(iconSize),
                    color = InProgressOrange,
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
            }

            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color.Gray,
            )
        }
    }
}

