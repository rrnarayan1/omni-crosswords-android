package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import com.rohanNarayan.omnicrosswords.ui.utils.smallHorizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.verticalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordScreen(dataViewModel: CrosswordDataViewModel, settingsVm: SettingsViewModel,
                    crosswordId: String?, goBack: () -> Unit) {
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
        val widthInDp: Float = with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp().value
        }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = state.focusedTag != -1,
                    enter = slideInVertically(initialOffsetY = { it }), // Slide up from bottom
                    exit = slideOutVertically(targetOffsetY = { it })   // Slide down out of view
                ) {
                    CompactCrosswordKeyboard(
                        onKeyClick = { vm.onInputReceived(it) },
                        onDelete = { vm.onBackspace() }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        if (keyEvent.key == Key.Backspace) {
                            vm.onBackspace()
                        } else {
                            val char = keyEvent.utf16CodePoint.toChar()
                            vm.onInputReceived(char)
                        }
                        true
                    } else {
                        false
                    }
                }) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = verticalPadding)) {
                        val boxWidth = (widthInDp - smallHorizontalPadding.value * 2) / crossword.width

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
                                            onClick = { vm.onCellTap(tag) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    CrosswordClueToolbar(
                        vm = vm, activeClue = activeClue,
                        clueFontSize = settings.value.clueFontSize
                    )
                }
            }
        }
    }
}
