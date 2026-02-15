package com.rohanNarayan.omnicrosswords.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit.Companion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordScreen(dataViewModel: CrosswordDataViewModel, settingsVm: SettingsViewModel, crosswordId: String?) {
    if (crosswordId == null) {
        return
    }
    val crosswordState = dataViewModel.localGet(crosswordId).collectAsState(initial = null)

    if (crosswordState.value != null) {
        val crossword = crosswordState.value!!
        val vm = viewModel {
            CrosswordViewModel(crossword, dataVm = dataViewModel, settingsVm = settingsVm)
        }
        val state by vm.uiState.collectAsState()
        val activeClue = remember(state) {
            vm.getActiveClue()
        }
        val settings = settingsVm.settings.collectAsState()
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        Scaffold(
            topBar = {
                CrosswordTopBar(crossword = crossword,
                    isSolved = state.isSolved,
                    isErrorTrackingEnabled = state.errorTrackingEnabled,
                    onErrorTrackingChange = { vm.toggleErrorTracking() },
                    isRebusModeEnabled = state.isRebusModeEnabled,
                    onRebusModeChange = { vm.toggleRebusMode() }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.padding(10.dp).fillMaxWidth()) {
                    CrosswordTextField(vm = vm, focusRequester = focusRequester)
                    val boxWidth = ((LocalConfiguration.current.screenWidthDp - 20) / crossword.width).toInt()

                    Column(modifier = Modifier.align(Alignment.Center)) {
                        for (row in 0 until crossword.height) {
                            Row {
                                for (column in 0 until crossword.width) {
                                    val tag = (row * crossword.width + column).toInt()
                                    val letter = state.entry.getOrElse(tag) { "" }
                                    val isHighlighted = state.highlighted.contains(tag)
                                    CrosswordCellView(
                                        value = letter,
                                        correctValue = crossword.solution[tag],
                                        errorTrackingEnabled = state.errorTrackingEnabled,
                                        symbol = crossword.symbols[tag],
                                        isFocusedTag = (tag == state.focusedTag),
                                        isHighlighted = isHighlighted,
                                        boxWidth = boxWidth,
                                        onClick = {
                                            vm.onCellTap(tag)
                                            focusRequester.requestFocus()
                                            keyboardController?.show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxSize().height(25.dp).padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        Box(modifier = Modifier.clickable { vm.toggleDirection() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                                contentDescription = "Toggle Direction"
                            )
                        }

                        Box(modifier = Modifier.clickable { vm.solveCell() }) {
                            Icon(imageVector = Icons.Default.Support,
                                contentDescription = "Solve Cell"
                            )
                        }
                    }

                    ScrollableText(text = activeClue ?: "", fontSize = settings.value.clueFontSize)

                    Row {
                        Box(modifier = Modifier.clickable { vm.goToPreviousClue() }) {
                            Icon(imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Clue")
                        }
                        Box(modifier = Modifier.clickable { vm.goToNextClue() }) {
                            Icon(imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Clue")
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun CrosswordCellView(
    value: String,
    correctValue: String,
    errorTrackingEnabled: Boolean,
    symbol: Int,
    isFocusedTag: Boolean,
    isHighlighted: Boolean,
    boxWidth: Int,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val isEditable = symbol != -1
    val isCellRed = errorTrackingEnabled && value != "" && value != correctValue
    val backgroundColor = when {
        !isEditable -> Color.Black
        isCellRed && isFocusedTag -> Color(0xBAFF3B30)
        isCellRed && isHighlighted -> Color(0x80FF3B30)
        isCellRed -> Color(0x64FF3B30)
        isDarkMode && isFocusedTag -> Color(0xCA0A84FF)
        isDarkMode && isHighlighted -> Color(0x800A84FF)
        !isDarkMode && isFocusedTag -> Color(0x960A84FF)
        !isDarkMode && isHighlighted -> Color(0x400A84FF)
        isDarkMode -> Color(0xFF636366)
        else -> MaterialTheme.colorScheme.surface // white or grey
    }

    Box(
        modifier = Modifier
            .size(boxWidth.dp)
            .border(0.4.dp, Color.Black)
            .background(backgroundColor)
            .clickable(enabled = isEditable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isEditable) {
            val fontSize = if (value.length == 1) boxWidth * 0.8 else (boxWidth.toFloat() / value.length)
            Text(
                text = value,
                style = TextStyle(fontSize = fontSize.toInt().sp)
            )

            if (symbol % 1000 != 0) {
                val clueNum = symbol % 1000
                Text(
                    text = clueNum.toString(),
                    style = TextStyle(fontSize = (boxWidth*0.2).sp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun ScrollableText(text: String, fontSize: Int) {
    val scrollState = rememberScrollState()

    LaunchedEffect(text) {
        scrollState.scrollTo(0)
    }

    Column(
        modifier = Modifier
            .width((LocalConfiguration.current.screenWidthDp - 120).dp)
            .height(25.dp)
            .padding(horizontal = 15.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            lineHeight = (fontSize + 1).sp,
            fontSize = fontSize.sp
        )
    }
}
