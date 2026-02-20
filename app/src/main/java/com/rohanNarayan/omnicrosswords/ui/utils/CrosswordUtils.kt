package com.rohanNarayan.omnicrosswords.ui.utils

import com.rohanNarayan.omnicrosswords.ui.settings.SettingsState
import kotlin.text.isNotEmpty

fun getProgress(symbols: List<Int>, entry: List<String>): Float {
    val fillableSquares: Int = symbols.filter { it != -1 }.size
    val filledSquares: Int = entry.filter { it != "." }.filter { it.isNotEmpty() }.size
    return filledSquares.toFloat() / fillableSquares
}

fun getClueId(tag: Int, goingAcross: Boolean, tagToClueMap: List<Map<String, String>>): String? {
    if (tag == -1) {
        return null
    }
    val directionalLetter = if (goingAcross) "A" else "D"
    val clueId = tagToClueMap[tag][directionalLetter]
    return clueId
}

fun isGoingAcross(clueId: String): Boolean {
    return clueId.last() == 'A'
}

fun getNextClueID(tag: Int, goingAcross: Boolean, clues: Map<String, String>,
                  tagToClueMap: List<Map<String, String>>): String {
    val directionalLetter: String = if (goingAcross) "A" else "D"
    val currentClueID: String? = getClueId(tag = tag, goingAcross = goingAcross,
        tagToClueMap = tagToClueMap)
    val currentClueNum: Int = currentClueID?.dropLast(1)?.toInt()!!

    for (i in currentClueNum+1 until clues.size) {
        val trialClueID: String = i.toString()+directionalLetter
        if (clues[trialClueID]?.isNotEmpty() ?: false) {
            return trialClueID
        }
    }
    for (i in 1 until clues.size) {
        val trialClueID: String = i.toString() + (if(directionalLetter == "A") "D" else "A")
        if (clues[trialClueID]?.isNotEmpty() ?: false) {
            return trialClueID
        }
    }
    return "1A" // should never get here
}

fun getPreviousClueID(tag: Int, goingAcross: Boolean, clues: Map<String, String>,
                      tagToClueMap: List<Map<String, String>>): String {
    val directionalLetter: String = if (goingAcross) "A" else "D"
    val currentClueID: String? = getClueId(tag = tag, goingAcross = goingAcross,
        tagToClueMap = tagToClueMap)
    val currentClueNum: Int = currentClueID?.dropLast(1)?.toInt()!!
    for (i in (1..<currentClueNum).reversed()) {
        val trialClueID: String = i.toString()+directionalLetter
        if (clues[trialClueID]?.isNotEmpty() ?: false) {
            return trialClueID
        }
    }
    return 1.toString() + if(directionalLetter == "A") "D" else "A"
}

data class TagAndDirection(val tag: Int, val goingAcross: Boolean)

fun getNextTagAndCheck(startTag: Int, currentTag: Int, goingAcross: Boolean, checkCluesForwards: Boolean,
                       crosswordEntry: List<String>, tagToClueMap: List<Map<String, String>>,
                       clueToTagsMap: Map<String, List<Int>>, clues: Map<String, String>, crosswordWidth: Int,
                       settings: SettingsState): TagAndDirection {
    var goingAcross = goingAcross
    var potentialTag = startTag
    //val currentClueId = getClueId(crossword = _crossword, tag = currentTag, goingAcross = currentState.goingAcross)
    if (potentialTag >= crosswordEntry.size // at the end of the puzzle
        || tagToClueMap[potentialTag].isEmpty() // there are no clues at this tag
        || !crosswordEntry[potentialTag].isEmpty() // there's something at this cell
        || potentialTag % crosswordWidth == 0) {
        if (settings.skipCompletedCells) {
            var oldTag = currentTag
            for (unused in 1 until crosswordEntry.size) {
                if (potentialTag >= crosswordEntry.size
                    || crosswordEntry[potentialTag] == "."
                    || tagToClueMap[potentialTag].isEmpty()
                    || ((potentialTag + 1) % crosswordWidth == 0 && !checkCluesForwards)
                ) {
                    // if we're checking the end, start checking again from the start
                    // if we're at a block, start checking the next clue
                    // if we're beyond the bounds of the puzzle, start checking next clue
                    // if we're going backwards and we've reached a clue that ends at the end of a row, go back a clue
                    val possibleNextClueId: String =
                        if (checkCluesForwards)
                            getNextClueID(tag = oldTag, goingAcross = goingAcross, clues = clues,
                                tagToClueMap = tagToClueMap)
                        else getPreviousClueID(tag = oldTag, goingAcross = goingAcross, clues = clues,
                            tagToClueMap = tagToClueMap)
                    goingAcross = isGoingAcross(possibleNextClueId)
                    potentialTag = clueToTagsMap[possibleNextClueId]!!.min()
                } else if (crosswordEntry[potentialTag].isEmpty()) {
                    // if the potential tag is empty, go there
                    return TagAndDirection(tag = potentialTag, goingAcross = goingAcross)
                } else {
                    // possibleTag's cell is full, so move to next cell
                    oldTag = potentialTag
                    potentialTag = getNextTag(tag = potentialTag, goingAcross = goingAcross,
                        crosswordLength = crosswordWidth)
                }
            }
            return TagAndDirection(tag = potentialTag, goingAcross = goingAcross)
        } else if (potentialTag >= crosswordEntry.size || tagToClueMap[potentialTag].isEmpty()) {
            // they don't want to skip completed cells, so when we're at the end of the puzzle/at a square, go to start of the next clue
            val possibleNextClueId: String =
                if (checkCluesForwards)
                    getNextClueID(tag = currentTag, goingAcross = goingAcross, clues = clues,
                        tagToClueMap = tagToClueMap)
                else
                    getPreviousClueID(tag = currentTag, goingAcross = goingAcross, clues = clues,
                        tagToClueMap = tagToClueMap)
            goingAcross = isGoingAcross(possibleNextClueId)
            potentialTag = clueToTagsMap[possibleNextClueId]!!.min()
            return TagAndDirection(tag = potentialTag, goingAcross = goingAcross)
        } else {
            // they don't want to skip completed cells, and we're checking a valid square, so just go to that square
            return TagAndDirection(tag = potentialTag, goingAcross = goingAcross)
        }
    } else {
        // empty cell, so just go there
        return TagAndDirection(tag = potentialTag, goingAcross = goingAcross)
    }
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
