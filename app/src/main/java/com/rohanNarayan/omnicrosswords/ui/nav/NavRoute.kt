package com.rohanNarayan.omnicrosswords.ui.nav

sealed class NavRoute(val route: String) {
    object List: NavRoute("list")
    object Crossword: NavRoute("Crossword")
    object Settings: NavRoute("Settings")
}