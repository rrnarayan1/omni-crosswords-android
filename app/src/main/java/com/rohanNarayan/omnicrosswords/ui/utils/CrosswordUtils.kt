package com.rohanNarayan.omnicrosswords.ui.utils

import com.rohanNarayan.omnicrosswords.data.Crossword

fun getProgress(symbols: List<Int>, entry: List<String>): Float {
    val fillableSquares: Int = symbols.filter { it != -1 }.size
    val filledSquares: Int = entry.filter { it != "." }.filter { it.isNotEmpty() }.size
    return filledSquares.toFloat() / fillableSquares
}

fun getClueId(crossword: Crossword, tag: Int, goingAcross: Boolean): String? {
    if (tag == -1) {
        return null
    }
    val directionalLetter = if (goingAcross) "A" else "D"
    val clueId = crossword.tagToClueMap[tag][directionalLetter]
    return clueId
}

fun getNextTag(tag: Int, goingAcross: Boolean, crosswordLength: Int): Int {
    return if (goingAcross) {
        tag + 1
    } else {
        tag + crosswordLength
    }
}

fun getPreviousTag(tag: Int, goingAcross: Boolean, crosswordLength: Int): Int {
    return if (goingAcross) {
        tag - 1
    } else {
        tag - crosswordLength
    }
}
