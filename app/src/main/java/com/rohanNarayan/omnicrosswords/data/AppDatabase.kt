package com.rohanNarayan.omnicrosswords.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Crossword::class], version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ])
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun crosswordDao(): CrosswordDao
}