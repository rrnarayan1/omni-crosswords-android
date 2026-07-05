package com.rohanNarayan.omnicrosswords.data

import com.google.gson.annotations.SerializedName

data class CrosswordUploadResponse(
    @SerializedName("crossword_outlet_name") val outletName: String,
    val date: Long,
    val author: String,
    val title: String,
    val width: Long,
    val height: Long,
    val copyright: String,
    val notes: String,
    val solution: List<String>,
    val clues: Map<String, String>,
    @SerializedName("tag_to_clue_map") val tagToClueMap: List<Map<String, String>>,
    val symbols: List<Int>
)