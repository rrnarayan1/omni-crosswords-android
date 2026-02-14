package com.rohanNarayan.omnicrosswords.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CrosswordDao {
    @Query("SELECT * from crossword WHERE is_solved IN (0, :showSolved) ORDER BY date DESC, outlet_name ASC")
    fun getAll(showSolved: Boolean): Flow<List<Crossword>>

    @Query("SELECT * from crossword WHERE id= :id")
    fun get(id: String): Flow<Crossword>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg crosswords: Crossword)

    @Update
    fun update(vararg crosswords: Crossword)

    @Delete
    fun delete(crossword: Crossword)
}