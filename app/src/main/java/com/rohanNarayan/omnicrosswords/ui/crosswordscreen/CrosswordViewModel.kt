package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import com.rohanNarayan.omnicrosswords.ui.utils.getClueId
import com.rohanNarayan.omnicrosswords.ui.utils.getNextTag
import com.rohanNarayan.omnicrosswords.ui.utils.getPreviousTag
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

    fun getActiveClue(): String? {
        val currentState = _uiState.value
        val clueId = getClueId(crossword = _crossword,
            tag = currentState.focusedTag,
            goingAcross = currentState.goingAcross)
        return _crossword.clues[clueId]
    }

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

    fun changeFocus(tag: Int) {
        if (tag < 0 || tag >= _crossword.symbols.size || (_crossword.tagToClueMap[tag].isEmpty())) {
            return
        }
        val currentState = _uiState.value
        var newGoingAcross = currentState.goingAcross
        val direction = if (currentState.goingAcross) "D" else "A"
        if (_crossword.tagToClueMap[tag][direction]?.isEmpty() ?: true) {
            // if we're going across and across clue doesn't exist, switch to going down
            newGoingAcross = !newGoingAcross
        }
        _uiState.update { it.copy(focusedTag = tag, goingAcross = newGoingAcross,
            isRebusModeEnabled = false) }
        setHighlighting()
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

    fun setHighlighting() {
        val currentState = _uiState.value
        val clueId = getClueId(crossword = _crossword,
            tag = currentState.focusedTag,
            goingAcross = currentState.goingAcross)
        val tagsForClue = _crossword.clueToTagsMap[clueId]
        _uiState.update {
            it.copy(highlighted = tagsForClue?.toSet() ?: emptySet())
        }
    }

    fun solveCell() {
        val currentState = _uiState.value

        val newEntry = currentState.entry.toMutableList().apply {
            this[currentState.focusedTag] = _crossword.solution[currentState.focusedTag]
        }
        goToNextTagAndCheck()
        saveEntry(newEntry)
    }

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
            val newEntry = currentState.entry.toMutableList().apply {
                this[currentState.focusedTag] = ""
            }
            saveEntry(newEntry)
        } else {
            val previousTag: Int = getPreviousTag(tag = currentState.focusedTag,
                goingAcross = currentState.goingAcross, crosswordLength = _crossword.width.toInt())

            if (previousTag >= 0 && previousTag < _crossword.symbols.count() &&
                _crossword.symbols[previousTag] != -1) {
                // previous tag is valid, so clear it and go there
                val newEntry = currentState.entry.toMutableList().apply {
                    this[previousTag] = ""
                }
                saveEntry(newEntry)
                changeFocus(previousTag)
            } else {
                // just go somewhere
                for (potentialTag in (0..<previousTag).reversed()) {
                    if (_crossword.symbols[potentialTag] != -1) {
                        changeFocus(potentialTag)
                    }
                }
            }
        }
    }

    fun goToNextTagAndCheck() {
        val currentState = _uiState.value
        val nextTag = getNextTag(tag = currentState.focusedTag,
            goingAcross = currentState.goingAcross,
            crosswordLength = _crossword.width.toInt())
        goToTagAndCheck(startTag = nextTag, checkCluesForwards = true)
    }

    fun goToTagAndCheck(startTag: Int, checkCluesForwards: Boolean) {
        val currentState = _uiState.value
        val crosswordWidth = _crossword.width.toInt()
        val currentTag = currentState.focusedTag
        var potentialTag = startTag
        //val currentClueId = getClueId(crossword = _crossword, tag = currentTag, goingAcross = currentState.goingAcross)
        if (potentialTag >= _crossword.symbols.size // at the end of the puzzle
            || _crossword.tagToClueMap[potentialTag].isEmpty() // there are no clues
            || !currentState.entry[potentialTag].isEmpty() // there's something at this cell
            || potentialTag % crosswordWidth == 0) {
            if (_settingsVm.settings.value.skipCompletedCells) {
                var oldTag = currentTag
                for (unused in 1..<_crossword.symbols.size) {
                    if (potentialTag >= _crossword.entry.size
                        || _crossword.symbols[potentialTag] == -1
                        || _crossword.tagToClueMap[potentialTag].isEmpty()
                        || ((potentialTag + 1) % crosswordWidth == 0 && !checkCluesForwards)
                    ) {
                        // if we're checking the end, start checking again from the start
                        // if we're at a block, start checking the next clue
                        // if we're beyond the bounds of the puzzle, start checking next clue
                        // if we're going backwards and we've reached a clue that ends at the end of a row, go back a clue
                        val possibleNextClueId: String = if (checkCluesForwards) getNextClueID(oldTag) else getPreviousClueID(oldTag)
                        potentialTag = _crossword.clueToTagsMap[possibleNextClueId]!!.min()
                    } else if (currentState.entry[potentialTag].isEmpty()) {
                        // if the potential tag is empty, go there
                        changeFocus(potentialTag)
                        return
                    } else {
                        // possibleTag's cell is full, so move to next cell
                        oldTag = potentialTag
                        potentialTag = getNextTag(tag = potentialTag,
                            goingAcross = _uiState.value.goingAcross,
                        crosswordLength = _crossword.width.toInt())
                    }
                }
                changeFocus(currentTag)
                return
            } else if (potentialTag >= _crossword.symbols.size
                || _crossword.tagToClueMap[potentialTag].isEmpty()) {
                // they don't want to skip completed cells, so when we're at the end of the puzzle/at a square, go to start of the next clue
                val possibleNextClueId: String = if (checkCluesForwards) getNextClueID(currentTag) else getPreviousClueID(currentTag)
                potentialTag = _crossword.clueToTagsMap[possibleNextClueId]!!.min()
                changeFocus(potentialTag)
                return
            } else {
                // they don't want to skip completed cells, and we're checking a valid square, so just go to that square
                changeFocus(potentialTag)
                return
            }
        } else {
            // empty cell, so just go there
            changeFocus(potentialTag)
            return
        }
    }

    fun getNextClueID(tag: Int): String {
        val currentState = _uiState.value
        val directionalLetter: String = if (currentState.goingAcross) "A" else "D"
        val currentClueID = getClueId(crossword = _crossword, tag = tag, goingAcross = currentState.goingAcross)
        val currentClueNum: Int = currentClueID?.dropLast(1)?.toInt()!!
        for (i in currentClueNum+1 until _crossword.clues.size) {
            val trialClueID: String = i.toString()+directionalLetter
            if (_crossword.clues[trialClueID]?.isNotEmpty() ?: false) {
                return trialClueID
            }
        }
        _uiState.update {
            it.copy(goingAcross = !it.goingAcross)
        }
        for (i in 1 until _crossword.clues.size) {
            val trialClueID: String = i.toString() + (if(directionalLetter == "A") "D" else "A")
            if (_crossword.clues[trialClueID]?.isNotEmpty() ?: false) {
                return trialClueID
            }
        }
        return "1A" // should never get here
    }

    fun getPreviousClueID(tag: Int): String {
        val currentState = _uiState.value
        val directionalLetter: String = if (currentState.goingAcross) "A" else "D"
        val currentClueID = getClueId(crossword = _crossword, tag = tag, goingAcross = currentState.goingAcross)
        val currentClueNum: Int = currentClueID?.dropLast(1)?.toInt()!!
        for (i in (1..<currentClueNum).reversed()) {
            val trialClueID: String = i.toString()+directionalLetter
            if (_crossword.clues[trialClueID]?.isNotEmpty() ?: false) {
                return trialClueID
            }
        }
        _uiState.update {
            it.copy(goingAcross = !it.goingAcross)
        }
        return 1.toString() + if(directionalLetter == "A") "D" else "A"
    }

    fun goToNextClue() {
        val currentState = _uiState.value
        val nextClueId = getNextClueID(currentState.focusedTag)
        val nextClueStartTag: Int = _crossword.clueToTagsMap[nextClueId]!!.min()
        goToTagAndCheck(startTag = nextClueStartTag, checkCluesForwards = true)
    }

    fun goToPreviousClue() {
        val currentState = _uiState.value
        val previousClueId = getPreviousClueID(currentState.focusedTag)
        val previousClueStartTag: Int = _crossword.clueToTagsMap[previousClueId]!!.min()
        goToTagAndCheck(startTag = previousClueStartTag, checkCluesForwards = false)
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