package com.rohanNarayan.omnicrosswords.ui.utils

import com.rohanNarayan.omnicrosswords.data.Crossword

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
