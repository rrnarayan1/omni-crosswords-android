package com.rohanNarayan.omnicrosswords.ui.listscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.ui.nav.NavRoute
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import com.rohanNarayan.omnicrosswords.ui.theme.InProgressOrange
import com.rohanNarayan.omnicrosswords.ui.theme.SuccessGreen
import com.rohanNarayan.omnicrosswords.ui.utils.getProgress
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.toFormattedDate
import com.rohanNarayan.omnicrosswords.ui.utils.toTime
import com.rohanNarayan.omnicrosswords.ui.utils.verticalPadding
import kotlinx.coroutines.launch

@Composable
fun CrosswordListItem(navController: NavController, crossword: Crossword, settingsVm: SettingsViewModel,
                      onDismiss: () -> Unit) {
    val settings = settingsVm.settings.collectAsState()
    val outletName = crossword.outletName
    val formattedDate = toFormattedDate(crossword.date)
    val title = "$outletName - $formattedDate"
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    fun resetSwipeState() {
        scope.launch {
            swipeToDismissBoxState.reset()
        }
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    Row(modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.End) {
                        IconButton(modifier = Modifier
                            .background(Color.Gray)
                            .padding(horizontal = horizontalPadding),
                            onClick = { resetSwipeState() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                tint = Color.White
                            )
                        }
                        IconButton(modifier = Modifier
                            .background(Color.Red)
                            .padding(horizontal = horizontalPadding),
                            onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove item",
                                tint = Color.White
                            )
                        }
                    }

                }
                SwipeToDismissBoxValue.StartToEnd -> {}
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .clickable { navController.navigate(route = NavRoute.Crossword.route + "?crosswordId=${crossword.id}") },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title)
            val progress = getProgress(symbols = crossword.symbols, entry = crossword.entry)

            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconSize = 20.dp

                if (progress > 0 && settings.value.showTimer) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = horizontalPadding),
                        text = toTime(crossword.elapsedTime)
                    )
                }

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

                if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.Settled) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Open",
                        tint = Color.Gray,
                    )
                }
            }
        }
    }
}

