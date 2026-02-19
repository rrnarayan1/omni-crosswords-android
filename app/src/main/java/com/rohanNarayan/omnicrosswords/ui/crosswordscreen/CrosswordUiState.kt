package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

data class CrosswordUiState(
    val entry: List<String> = emptyList(),
    val focusedTag: Int = -1,
    val highlighted: Set<Int> = emptySet(),
    val goingAcross: Boolean = true,
    val errorTrackingEnabled: Boolean = false,
    val isSolved: Boolean = false,
    val isRebusModeEnabled: Boolean = false
)
