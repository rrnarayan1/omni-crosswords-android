package com.rohanNarayan.omnicrosswords.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordListView(navController: NavController, settingsVm: SettingsViewModel, dataViewModel: CrosswordDataViewModel) {
    val settings = settingsVm.settings.collectAsState()
    val crosswordListFlow = dataViewModel.localGetAllRecords(showSolved = settings.value.showSolvedPuzzles)
    val crosswordList = crosswordListFlow.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
       fetchCrosswords(vm = dataViewModel, settingsVm = settingsVm, existingCrosswords = crosswordListFlow)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crosswords", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(route = Screen.Settings.route) }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(crosswordList.value) { crossword ->
                CrosswordListItemView(navController = navController, crossword = crossword)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}

suspend fun fetchCrosswords(vm: CrosswordDataViewModel, settingsVm: SettingsViewModel, existingCrosswords: Flow<List<Crossword>>) {
    val maybeExistingCrosswords: List<Crossword>? = existingCrosswords.firstOrNull()
    val existingCrosswordsIds: Set<String> =
        maybeExistingCrosswords?.map { it.id }?.toSet() ?: emptySet()
    val maybeLatestDate: Long? = maybeExistingCrosswords?.firstOrNull()?.date

    val coldStartTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    coldStartTime.add(Calendar.DATE, -7)

    val subscribedOutlets = settingsVm.settings.value.subscribedOutlets
    val fetchAfter: Long = maybeLatestDate ?: coldStartTime.time.toInstant().epochSecond

    val remoteCrosswords: Flow<List<Crossword>> = vm.remoteQuery(fetchAfter, subscribedOutlets)
    remoteCrosswords.collect { remoteCrosswordList ->
        remoteCrosswordList.forEach { remoteCrossword ->
            if (!existingCrosswordsIds.contains(remoteCrossword.id)) {
                vm.localInsert(remoteCrossword)
            }
        }
        maybeDeleteCrosswords(vm = vm, settingsVm = settingsVm, existingCrosswords = maybeExistingCrosswords)
    }
}

fun maybeDeleteCrosswords(vm: CrosswordDataViewModel, settingsVm: SettingsViewModel, existingCrosswords: List<Crossword>?) {
    if (existingCrosswords == null) {
        return
    }
    val subscribedOutlets = settingsVm.settings.value.subscribedOutlets
    val deletionDays = settingsVm.settings.value.deletionDays
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.add(Calendar.DATE, -(deletionDays+1))
    val deletionDate: Date? = if (deletionDays != -1) calendar.time else null

    existingCrosswords.forEach {
        val crosswordDate = Date(it.date * 1000)
        if (deletionDate != null && deletionDate > crosswordDate) {
            vm.localDelete(it)
        } else if (!subscribedOutlets.contains(it.outletName)) {
            vm.localDelete(it)
        }
    }
}
