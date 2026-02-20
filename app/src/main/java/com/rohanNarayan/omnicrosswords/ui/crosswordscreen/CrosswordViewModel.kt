package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsState
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import com.rohanNarayan.omnicrosswords.ui.utils.getClueId
import com.rohanNarayan.omnicrosswords.ui.utils.getNextClueID
import com.rohanNarayan.omnicrosswords.ui.utils.getNextTag
import com.rohanNarayan.omnicrosswords.ui.utils.getNextTagAndCheck
import com.rohanNarayan.omnicrosswords.ui.utils.getPreviousClueID
import com.rohanNarayan.omnicrosswords.ui.utils.getPreviousTag
import com.rohanNarayan.omnicrosswords.ui.utils.isGoingAcross
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.get

class CrosswordViewModel(crossword: Crossword, dataVm: CrosswordDataViewModel, settingsVm: SettingsViewModel): ViewModel() {
    private var _crossword: Crossword = crossword
    private var _dataVm: CrosswordDataViewModel = dataVm
    private var _settingsVm: SettingsViewModel = settingsVm
    private val _uiState = MutableStateFlow(CrosswordUiState())
    val uiState: StateFlow<CrosswordUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                entry = _crossword.entry,
                errorTrackingEnabled = _settingsVm.settings.value.defaultErrorTracking,
                isSolved = _crossword.isSolved
            )
        }
    }

    fun getActiveClue(): String? {
        val currentState = _uiState.value
        val clueId: String? = getClueId(tag = currentState.focusedTag, goingAcross = currentState.goingAcross,
            tagToClueMap = _crossword.tagToClueMap)
        return _crossword.clues[clueId]
    }

    //region Changing Focus Basics

    /*
     If they tapped on the focused cell, toggle the direction. If not, change focus to that cell
     */
    fun onCellTap(tag: Int) {
        val currentState = _uiState.value
        if (_crossword.symbols[tag] != -1) {
            if (currentState.focusedTag == tag) {
                toggleDirection()
            } else {
                changeFocus(tag)
            }
        }
    }

    /*
     Change focus to specified tag. If goingAcross is specified, change it. If not, leave it alone
     */
    fun changeFocus(tag: Int, goingAcross: Boolean? = null) {
        if (tag < 0 || tag >= _crossword.symbols.size || (_crossword.tagToClueMap[tag].isEmpty())) {
            return
        }
        val currentState = _uiState.value
        var newGoingAcross = goingAcross ?: currentState.goingAcross
        val currentDirection = if (currentState.goingAcross) "D" else "A"
        if (_crossword.tagToClueMap[tag][currentDirection]?.isEmpty() ?: true) {
            // if we're going across and across clue doesn't exist, switch to going down
            newGoingAcross = !newGoingAcross
        }
        _uiState.update {
            it.copy(focusedTag = tag, goingAcross = newGoingAcross, isRebusModeEnabled = false)
        }
        setHighlighting()
    }

    /*
     Reset highlighting based on current tag and goingAcross
     */
    fun setHighlighting() {
        val currentState = _uiState.value
        val clueId: String? = getClueId(tag = currentState.focusedTag, goingAcross = currentState.goingAcross,
            tagToClueMap = _crossword.tagToClueMap)
        val tagsForClue: List<Int>? = _crossword.clueToTagsMap[clueId]
        _uiState.update {
            it.copy(highlighted = tagsForClue?.toSet() ?: emptySet())
        }
    }

    //endregion

    //region Crossword Setting Toggles

    fun toggleErrorTracking() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(errorTrackingEnabled = !currentState.errorTrackingEnabled)
        }
    }

    fun toggleRebusMode(newValue: Boolean? = null) {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(isRebusModeEnabled = newValue ?: !currentState.isRebusModeEnabled)
        }
    }

    //endregion

    //region crossword clue toolbar actions
    fun solveCell() {
        val currentState = _uiState.value
        val focusedTag = currentState.focusedTag

        val newEntry = currentState.entry.toMutableList()
        newEntry[focusedTag] = _crossword.solution[focusedTag]

        goToNextTagAndCheck()
        saveEntry(newEntry)
    }

    fun toggleDirection() {
        val currentState = _uiState.value
        val newDirection = if (currentState.goingAcross) "D" else "A"

        if (_crossword.tagToClueMap[currentState.focusedTag][newDirection]?.isEmpty() ?: true) {
            // if we're going to try to go to across and across clue doesn't exist, do nothing
            return
        }
        _uiState.update {
            it.copy(goingAcross = !it.goingAcross)
        }
        setHighlighting()
    }

    fun goToPreviousClue() {
        val currentState = _uiState.value

        val currentTag = currentState.focusedTag
        val previousClueId = getPreviousClueID(tag = currentTag, goingAcross = currentState.goingAcross,
            clues = _crossword.clues, tagToClueMap = _crossword.tagToClueMap)
        val newGoingAcross = isGoingAcross(previousClueId)
        _uiState.update {
            it.copy(goingAcross = newGoingAcross)
        }
        val previousClueStartTag: Int = _crossword.clueToTagsMap[previousClueId]!!.min()
        val nextTagAndDirection = getNextTagAndCheck(startTag = previousClueStartTag, currentTag = currentTag, goingAcross = newGoingAcross,
            checkCluesForwards = false,  crosswordEntry = currentState.entry, tagToClueMap = _crossword.tagToClueMap,
            clueToTagsMap = _crossword.clueToTagsMap, clues = _crossword.clues, crosswordWidth = _crossword.width.toInt(),
            settings = _settingsVm.settings.value)
        changeFocus(tag = nextTagAndDirection.tag, goingAcross = nextTagAndDirection.goingAcross)
    }

    fun goToNextClue() {
        val currentState = _uiState.value

        val currentTag = currentState.focusedTag
        val nextClueId = getNextClueID(tag = currentTag, goingAcross = currentState.goingAcross,
            clues = _crossword.clues, tagToClueMap = _crossword.tagToClueMap)
        val newGoingAcross = isGoingAcross(nextClueId)
        _uiState.update {
            it.copy(goingAcross = newGoingAcross)
        }
        val nextClueStartTag: Int = _crossword.clueToTagsMap[nextClueId]!!.min()
        val nextTagAndDirection = getNextTagAndCheck(startTag = nextClueStartTag, currentTag = currentTag, goingAcross = newGoingAcross,
            checkCluesForwards = true, crosswordEntry = currentState.entry, tagToClueMap = _crossword.tagToClueMap,
            clueToTagsMap = _crossword.clueToTagsMap, clues = _crossword.clues, crosswordWidth = _crossword.width.toInt(),
            settings = _settingsVm.settings.value)
        changeFocus(tag = nextTagAndDirection.tag, goingAcross = nextTagAndDirection.goingAcross)
    }
    //endregion

    //region Text Field manipulation
    fun onInputReceived(char: String) {
        val currentState = _uiState.value

        if (char == "." || char.length > 1 || currentState.isSolved) {
            return
        }

        if (char == " ") {
            if (_settingsVm.settings.value.spaceTogglesDirection) {
                toggleDirection()
            } else {
                goToNextTagAndCheck()
            }
            return
        }
        val newEntry = currentState.entry.toMutableList()
        if (currentState.isRebusModeEnabled) {
            newEntry[currentState.focusedTag] += char.uppercase()
        } else {
            newEntry[currentState.focusedTag] = char.uppercase()
            goToNextTagAndCheck()
        }
        saveEntry(newEntry)
    }

    fun onBackspace() {
        val currentState = _uiState.value
        if (currentState.entry[currentState.focusedTag].isNotEmpty()) {
            val newEntry = currentState.entry.toMutableList()
            newEntry[currentState.focusedTag] = ""
            saveEntry(newEntry)
        } else {
            val previousTag: Int = getPreviousTag(tag = currentState.focusedTag,
                goingAcross = currentState.goingAcross, crosswordLength = _crossword.width.toInt())

            if (previousTag >= 0 && previousTag < _crossword.symbols.count() &&
                _crossword.symbols[previousTag] != -1) {
                // previous tag is valid, so clear it and go there
                val newEntry = currentState.entry.toMutableList()
                newEntry[previousTag] = ""
                saveEntry(newEntry)
                changeFocus(previousTag)
            } else {
                // just go somewhere
                for (potentialTag in (0 until previousTag).reversed()) {
                    if (_crossword.symbols[potentialTag] != -1) {
                        changeFocus(potentialTag)
                    }
                }
            }
        }
    }
    //endregion

    fun goToNextTagAndCheck() {
        val currentState = _uiState.value
        val width = _crossword.width.toInt()
        val goingAcross = currentState.goingAcross
        val nextTag = getNextTag(tag = currentState.focusedTag, goingAcross = goingAcross,
            crosswordLength = width)
        val nextTagAndDirection = getNextTagAndCheck(startTag = nextTag, currentTag = currentState.focusedTag,
            goingAcross = goingAcross, checkCluesForwards = true, crosswordEntry = currentState.entry,
            tagToClueMap = _crossword.tagToClueMap, clueToTagsMap = _crossword.clueToTagsMap,
            clues = _crossword.clues, crosswordWidth = width, settings = _settingsVm.settings.value)
        changeFocus(tag = nextTagAndDirection.tag, goingAcross = nextTagAndDirection.goingAcross)
    }

    fun saveEntry(newEntry: List<String>) {
        val isSolved: Boolean = newEntry == _crossword.solution
        _uiState.update { it.copy(entry = newEntry, isSolved = isSolved) }
        val newCrossword: Crossword = _crossword.copy(entry = newEntry, isSolved = isSolved)
        viewModelScope.launch {
            _dataVm.localInsert(
                crossword = newCrossword
            )
        }
    }
}
