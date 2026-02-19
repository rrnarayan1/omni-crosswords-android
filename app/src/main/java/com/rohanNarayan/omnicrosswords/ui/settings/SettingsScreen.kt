package com.rohanNarayan.omnicrosswords.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import com.rohanNarayan.omnicrosswords.ui.utils.allOutlets
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel, goBack: () -> Unit) {
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
                        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = "Donate")
                    }

                    IconButton(onClick = { uriHandler.openUri("https://omnicrosswords.app") }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        ) {
            ToggleSetting(label = "Show solved puzzles", enabled = settings.showSolvedPuzzles) {
                vm.updateBooleanSetting(SettingsManager.SHOW_SOLVED_PUZZLES, it)
            }

            ToggleSetting(label = "Default error tracking",
                enabled = settings.defaultErrorTracking,
            ) {
                vm.updateBooleanSetting(SettingsManager.DEFAULT_ERROR_TRACKING, it)
            }

            ToggleSetting(label = "Skip completed cells", enabled = settings.skipCompletedCells) {
                vm.updateBooleanSetting(SettingsManager.SKIP_COMPLETED_CELLS, it)
            }

            ToggleSetting(label = "Space toggles direction", enabled = settings.spaceTogglesDirection) {
                vm.updateBooleanSetting(SettingsManager.SPACE_TOGGLES_DIRECTION, it)
            }

            val currentDays = settings.deletionDays
            val currentDayLabel = if (currentDays == -1) "Never" else "$currentDays days"

            PickerSetting(label = "Auto-delete puzzles after", currentLabel = currentDayLabel,
                options = vm.getDeletionDayOptions()
            ) {
                vm.updateIntSetting(SettingsManager.DELETION_DAYS, it)
            }

            PickerSetting(label = "Clue font size", currentLabel = settings.clueFontSize.toString(),
                options = vm.getFontSizeOptions()
            ) {
                vm.updateIntSetting(SettingsManager.CLUE_FONT_SIZE, it)
            }

            Button(onClick = { showSubscriptions = true }) {
                Text("Manage Subscriptions")
            }

            if (showSubscriptions) {
                SubscriptionsModal(
                    onDismiss = { showSubscriptions = false },
                    options = allOutlets.sorted(),
                    selectedOptions = settings.subscribedOutlets
                ) {
                    vm.toggleStringSetInclusion(SettingsManager.SUBSCRIBED_OUTLETS, it)
                }
            }
        }
    }
}
