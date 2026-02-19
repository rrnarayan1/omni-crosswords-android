package com.rohanNarayan.omnicrosswords.ui.settings

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {
    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SettingsState()
    )

    fun updateBooleanSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            settingsManager.updateBoolean(key, value)
        }
    }

    fun updateIntSetting(key: Preferences.Key<Int>, value: Int) {
        viewModelScope.launch {
            settingsManager.updateInt(key, value)
        }
    }

    fun toggleStringSetInclusion(key: Preferences.Key<Set<String>>, value: String) {
        viewModelScope.launch {
            settingsManager.toggleStringSetInclusion(key, value)
        }
    }

    fun getDeletionDayOptions(): Map<Int, String> {
        val dayValues = (2 ..< 22).toMutableList()
        val dayStrings = dayValues.map { "$it days" }.toMutableList()
        dayValues.add(-1)
        dayStrings.add("Never")
        return dayValues.zip(dayStrings).toMap()
    }

    fun getFontSizeOptions(): Map<Int, String> {
        val fontValues = (12 ..< 20).toList()
        val fontStrings = fontValues.map { it.toString() }
        return fontValues.zip(fontStrings).toMap()
    }
}