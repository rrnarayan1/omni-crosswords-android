package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.smallHorizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.verticalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordScreen(dataViewModel: CrosswordDataViewModel, settingsVm: SettingsViewModel, crosswordId: String?, goBack: () -> Unit) {
    if (crosswordId == null) {
        return
    }
    val crosswordState = dataViewModel.localGet(crosswordId).collectAsState(initial = null)

    if (crosswordState.value != null) {
        val crossword = crosswordState.value!!
        val vm = viewModel {
            CrosswordViewModel(crossword = crossword, dataVm = dataViewModel, settingsVm = settingsVm)
        }
        val state by vm.uiState.collectAsState()
        val activeClue = remember(state) {
            vm.getActiveClue()
        }
        val settings = settingsVm.settings.collectAsState()
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val widthInDp: Float = with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp().value
        }

        Scaffold(
            topBar = {
                CrosswordTopBar(crossword = crossword,
                    isSolved = state.isSolved,
                    isErrorTrackingEnabled = state.errorTrackingEnabled,
                    onErrorTrackingChange = { vm.toggleErrorTracking() },
                    isRebusModeEnabled = state.isRebusModeEnabled,
                    onRebusModeChange = { vm.toggleRebusMode() },
                    goBack = goBack
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Box(modifier = Modifier.fillMaxWidth().padding(vertical = verticalPadding)) {
                    CrosswordTextField(vm = vm, focusRequester = focusRequester)
                    val boxWidth = (widthInDp - smallHorizontalPadding.value*2) / crossword.width

                    Column(modifier = Modifier.align(Alignment.Center)) {
                        for (row in 0 until crossword.height) {
                            Row {
                                for (column in 0 until crossword.width) {
                                    val tag = (row * crossword.width + column).toInt()
                                    val letter = state.entry.getOrElse(tag) { "" }
                                    val isHighlighted = state.highlighted.contains(tag)
                                    CrosswordCell(
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

                CrosswordClueToolbar(vm = vm, activeClue = activeClue,
                    clueFontSize = settings.value.clueFontSize)
            }
        }
    }
}
