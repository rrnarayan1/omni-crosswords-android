package com.rohanNarayan.omnicrosswords.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Crossword (
    @PrimaryKey val id: String,
    @ColumnInfo(name="outlet_name") val outletName: String,
    @ColumnInfo(name="date") val date: Long,
    @ColumnInfo(name="title") val title: String,

    @ColumnInfo(name="author") val author: String,
    @ColumnInfo(name="copyright") val copyright: String,
    @ColumnInfo(name="notes") val notes: String,

    @ColumnInfo(name="height") val height: Long,
    @ColumnInfo(name="width") val width: Long,
    @ColumnInfo(name="clues") val clues: Map<String, String>,
    @ColumnInfo(name="solution") val solution: List<String>,
    @ColumnInfo(name="entry") var entry: List<String>,
    @ColumnInfo(name="symbols") val symbols: List<Int>,
    @ColumnInfo(name="tag_to_clue_map") val tagToClueMap: List<Map<String, String>>,
    @ColumnInfo(name="clue_to_tags_map") val clueToTagsMap: Map<String, List<Int>>,

    @ColumnInfo(name="is_solved") val isSolved: Boolean,
    @ColumnInfo(name="is_hidden") val isHidden: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Crossword

        if (date != other.date) return false
        if (height != other.height) return false
        if (width != other.width) return false
        if (id != other.id) return false
        if (outletName != other.outletName) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (copyright != other.copyright) return false
        if (notes != other.notes) return false
        if (clues != other.clues) return false
        if (!solution.equals(other.solution)) return false
        if (!entry.equals(other.entry)) return false
        if (!symbols.equals(other.symbols)) return false
        if (!tagToClueMap.equals(other.tagToClueMap)) return false
        if (!clueToTagsMap.equals(other.clueToTagsMap)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + outletName.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + copyright.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + clues.hashCode()
        result = 31 * result + solution.hashCode()
        result = 31 * result + entry.hashCode()
        result = 31 * result + symbols.hashCode()
        result = 31 * result + tagToClueMap.hashCode()
        result = 31 * result + clueToTagsMap.hashCode()
        return result
    }
}