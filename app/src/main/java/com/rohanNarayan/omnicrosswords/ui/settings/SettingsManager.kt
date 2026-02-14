package com.rohanNarayan.omnicrosswords.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")
val allOutlets = setOf("LA Times", "The Atlantic", "Newsday", "New Yorker", "USA Today",
    "Vox", "NYT Syndicated", "Universal", "NYT Mini", "Crossword Club")

class SettingsManager(private val context: Context) {

    // Define Keys
    companion object {
        val SHOW_SOLVED_PUZZLES = booleanPreferencesKey("show_solved_puzzles")
        val SKIP_COMPLETED_CELLS = booleanPreferencesKey("skip_completed_cells")
        val DEFAULT_ERROR_TRACKING = booleanPreferencesKey("error_tracking_default_on")
        val DELETION_DAYS = intPreferencesKey("deletion_days")
        val SPACE_TOGGLES_DIRECTION = booleanPreferencesKey("space_toggles_direction")
        val SUBSCRIBED_OUTLETS = stringSetPreferencesKey("subscribed_outlets")
        val CLUE_FONT_SIZE = intPreferencesKey("clue_font_size")
    }

    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        SettingsState(
            showSolvedPuzzles = prefs[SHOW_SOLVED_PUZZLES] ?: true,
            defaultErrorTracking = prefs[DEFAULT_ERROR_TRACKING] ?: false,
            skipCompletedCells = prefs[SKIP_COMPLETED_CELLS] ?: true,
            deletionDays = prefs[DELETION_DAYS] ?: 14,
            spaceTogglesDirection = prefs[SPACE_TOGGLES_DIRECTION] ?: false,
            subscribedOutlets = prefs[SUBSCRIBED_OUTLETS] ?: allOutlets,
            clueFontSize = prefs[CLUE_FONT_SIZE] ?: 14
        )
    }

    suspend fun updateBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    suspend fun updateInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    suspend fun toggleStringSetInclusion(key: Preferences.Key<Set<String>>, value: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[key]?.toMutableSet() ?: allOutlets.toMutableSet()
            if (set.contains(value)) {
                set.remove(value)
                prefs[key] = set
            } else {
                set.add(value)
                prefs[key] = set
            }
        }
    }
}

data class SettingsState(
    val showSolvedPuzzles: Boolean = true,
    val defaultErrorTracking: Boolean = false,
    val skipCompletedCells: Boolean = true,
    val deletionDays: Int = 14,
    val spaceTogglesDirection: Boolean = false,
    val subscribedOutlets: Set<String> = allOutlets,
    val clueFontSize: Int = 14,
)