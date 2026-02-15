package com.rohanNarayan.omnicrosswords.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val settings by vm.settings.collectAsState()
    var showSubscriptions by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                actions = {
                    IconButton(onClick = { uriHandler.openUri("https://ko-fi.com/rrnarayan1") }) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = "Donate")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)
            .padding(horizontal = 16.dp)) {
            // Toggle Row
            SettingToggle(
                label = "Show solved puzzles",
                enabled = settings.showSolvedPuzzles,
                onEnabledChange = { vm.updateBooleanSetting(SettingsManager.SHOW_SOLVED_PUZZLES, it)}
            )

            SettingToggle(
                label = "Default error tracking",
                enabled = settings.defaultErrorTracking,
                onEnabledChange = { vm.updateBooleanSetting(SettingsManager.DEFAULT_ERROR_TRACKING, it)}
            )

            SettingToggle(
                label = "Skip completed cells",
                enabled = settings.skipCompletedCells,
                onEnabledChange = { vm.updateBooleanSetting(SettingsManager.SKIP_COMPLETED_CELLS, it)}
            )

            SettingToggle(
                label = "Space toggles direction",
                enabled = settings.spaceTogglesDirection,
                onEnabledChange = { vm.updateBooleanSetting(SettingsManager.SPACE_TOGGLES_DIRECTION, it)}
            )

            val dayValues = (2 ..< 22).toMutableList()
            val dayStrings = dayValues.map { "$it days" }.toMutableList()
            dayValues.add(-1)
            dayStrings.add("Never")
            val currentDays = settings.deletionDays
            val currentDayLabel = if (currentDays == -1) "Never" else "$currentDays days"

            SettingPicker(
                label = "Auto-delete puzzles after",
                currentLabel = currentDayLabel,
                options = dayValues.zip(dayStrings).toMap(),
                onChange = { vm.updateIntSetting(SettingsManager.DELETION_DAYS, it)}
            )

            val fontValues = (12 ..< 20).toList()
            val fontStrings = fontValues.map { it.toString() }
            SettingPicker(
                label = "Clue font size",
                currentLabel = settings.clueFontSize.toString(),
                options = fontValues.zip(fontStrings).toMap(),
                onChange = { vm.updateIntSetting(SettingsManager.CLUE_FONT_SIZE, it)}
            )

            Button(onClick = { showSubscriptions = true }) {
                Text("Manage Subscriptions")
            }

            if (showSubscriptions) {
                SubscriptionsScreen(
                    onDismiss = { showSubscriptions = false },
                    options = allOutlets.sorted(),
                    selectedOptions = settings.subscribedOutlets,
                    onToggle = { vm.toggleStringSetInclusion(SettingsManager.SUBSCRIBED_OUTLETS, it) }
                )
            }
        }
    }
}

@Composable
fun SettingToggle(label: String, enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = enabled, onCheckedChange = onEnabledChange)
    }
}

@Composable
fun SettingPicker(label: String, currentLabel: String, options: Map<Int, String>, onChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(currentLabel)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "expand")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.keys.forEach { intOption ->
                    DropdownMenuItem(
                        text = { Text(options[intOption]!!) },
                        onClick = {
                            onChange(intOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onDismiss: () -> Unit,
    options: List<String>,
    selectedOptions: Set<String>,
    onToggle: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(options) { option ->
                val isChecked = selectedOptions.contains(option)

                ListItem(
                    headlineContent = { Text(option) },
                    trailingContent = {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null // Click handled by ListItem
                        )
                    },
                    modifier = Modifier.clickable { onToggle(option) }
                )
            }
        }
    }
}