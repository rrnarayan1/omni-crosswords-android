package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.ui.theme.SuccessGreen
import com.rohanNarayan.omnicrosswords.ui.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordTopBar(crossword: Crossword, isSolved: Boolean, isErrorTrackingEnabled: Boolean,
                    onErrorTrackingChange: () -> Unit, isRebusModeEnabled: Boolean, onRebusModeChange: () -> Unit,
                    goBack: () -> Unit) {
    var showSettings by remember { mutableStateOf(false) }
    val formattedDate = toFormattedDate(crossword.date)
    var title = "${crossword.outletName} - $formattedDate"
    if (isSolved) {
        title = "Solved: $title"
    }

    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { goBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { showSettings = true }) {
                Icon(imageVector = Icons.Default.SettingsInputComponent, contentDescription = "Settings")
            }

            if (showSettings) {
                CrosswordSettingsModal(
                    crossword = crossword,
                    onDismiss = { showSettings = false },
                    isErrorTrackingEnabled = isErrorTrackingEnabled,
                    onErrorTrackingChange = { onErrorTrackingChange() },
                    isRebusModeEnabled = isRebusModeEnabled,
                    onRebusModeChange = { onRebusModeChange() }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isSolved) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}
