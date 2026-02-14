package com.rohanNarayan.omnicrosswords.ui

sealed class Screen(val route: String) {
    object List: Screen("list")
    object Crossword: Screen("Crossword")
    object Settings: Screen("Settings")
}