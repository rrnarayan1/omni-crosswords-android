package com.rohanNarayan.omnicrosswords.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsManager
import com.rohanNarayan.omnicrosswords.ui.settings.SubscriptionsScreen
import com.rohanNarayan.omnicrosswords.ui.settings.allOutlets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordTopBar(crossword: Crossword, isSolved: Boolean, isErrorTrackingEnabled: Boolean,
                    onErrorTrackingChange: () -> Unit, isRebusModeEnabled: Boolean, onRebusModeChange: () -> Unit) {
    var showSettings by remember { mutableStateOf(false) }

    val formattedDate = DateTimeFormatter.ofPattern("EE M/d/yy")
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochSecond(crossword.date))
    var title = "${crossword.outletName} - $formattedDate"
    if (isSolved) {
        title = "Solved: $title"
    }
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = { showSettings = true }) {
                Icon(Icons.Default.SettingsInputComponent, contentDescription = "Settings")
            }

            if (showSettings) {
                CrosswordSettingsScreen(
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
            containerColor = if (isSolved) Color(0x9934C759) else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordSettingsScreen(crossword: Crossword, onDismiss: () -> Unit,
                            isErrorTrackingEnabled: Boolean, onErrorTrackingChange: () -> Unit,
                            isRebusModeEnabled: Boolean, onRebusModeChange: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Error Tracking",
                    modifier = Modifier.padding(horizontal = 5.dp))
                Switch(
                    checked = isErrorTrackingEnabled,
                    onCheckedChange = { onErrorTrackingChange() }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rebus Mode",
                    modifier = Modifier.padding(horizontal = 5.dp))
                Switch(
                    checked = isRebusModeEnabled,
                    onCheckedChange = { onRebusModeChange() }
                )
            }
            CrosswordInfo(crossword = crossword)
        }
    }
}

@Composable
fun CrosswordInfo(crossword: Crossword) {
    var crosswordInfo = crossword.title + "\n" + crossword.author
    if (!crossword.notes.isEmpty()) {
        crosswordInfo += ("\n" + crossword.notes)
    }
    if (!crossword.copyright.isEmpty()) {
        crosswordInfo += ("\n" + crossword.copyright)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(crosswordInfo, modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}