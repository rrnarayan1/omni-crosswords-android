package com.rohanNarayan.omnicrosswords.data

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class CrosswordDataViewModel(private val dao: CrosswordDao, private val db: FirebaseFirestore) : ViewModel() {
    fun localInsert(crossword: Crossword) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertAll(crossword)
        }
    }

    fun remoteQuery(fetchAfter: Long, subscribedOutlets: Set<String>): Flow<List<Crossword>> {
        val dateToFetchAfter: Date = Date.from(java.time.Instant.ofEpochSecond(fetchAfter))
        val query = db.collection("crosswords")
            .whereGreaterThanOrEqualTo("date", dateToFetchAfter)
            .whereIn("crossword_outlet_name", subscribedOutlets.toList())
            .limit(100)

        return query.snapshots()
            .map { snapshots ->
                snapshots.map { document ->
                    val height: Long = document.getLong("height")!!
                    val width: Long = document.getLong("width")!!
                    val tagToCluesList: List<Map<String, String>> = document.get("tag_to_clue_map") as List<Map<String, String>>
                    val clueToTagsMap: MutableMap<String, MutableList<Int>> = mutableMapOf()
                    for (tag in 0..<tagToCluesList.size) {
                        for (dir in listOf("A", "D")) {
                            if (tagToCluesList[tag].isNotEmpty()) {
                                val clueId: String? = tagToCluesList[tag][dir]
                                if (clueId != null) {
                                    if (clueToTagsMap[clueId] == null) {
                                        clueToTagsMap[clueId] = mutableListOf()
                                    }
                                    clueToTagsMap[clueId]?.add(tag)
                                }
                            }
                        }
                    }
                    val symbols: List<Int> = document.get("symbols") as List<Int>
                    val entry = MutableList((height * width).toInt()) { "" }
                    for (tag in 0..<symbols.size) {
                        if (symbols[tag] == -1) {
                            entry[tag] = "."
                        }
                    }

                    Crossword(
                        id = document.id,
                        outletName = document.getString("crossword_outlet_name")!!,
                        date = document.getDate("date")!!.toInstant().epochSecond,
                        title = document.getString("title")!!,
                        author = document.getString("author")!!,
                        copyright = document.getString("copyright")!!,
                        notes = document.getString("notes")!!,
                        height = height,
                        width = width,
                        clues = document.get("clues") as Map<String, String>,
                        solution = document.get("solution") as List<String>,
                        entry = entry,
                        symbols = symbols,
                        tagToClueMap = tagToCluesList,
                        clueToTagsMap = clueToTagsMap,
                        isSolved = false,
                        isHidden = false
                    )
                }
            }
    }

    fun localGetAllRecords(showSolved: Boolean): Flow<List<Crossword>> {
        return dao.getAll(showSolved = showSolved)
    }

    fun localGet(id: String): Flow<Crossword> {
        return dao.get(id)
    }

    fun localDelete(crossword: Crossword) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(crossword = crossword)
        }
    }

}