package com.rohanNarayan.omnicrosswords.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.listscreen.CrosswordListScreen
import com.rohanNarayan.omnicrosswords.ui.crosswordscreen.CrosswordScreen
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsManager
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsScreen
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel

@Composable
fun NavigationStack(dataViewModel: CrosswordDataViewModel) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(SettingsManager(context)) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = NavRoute.List.route) {
        composable(route = NavRoute.List.route) {
            CrosswordListScreen(
                navController = navController,
                settingsVm = settingsViewModel,
                dataViewModel = dataViewModel
            )
        }
        composable(route = NavRoute.Settings.route) {
            SettingsScreen(vm = settingsViewModel) {
                navController.popBackStack()
            }
        }
        composable(
            route = NavRoute.Crossword.route + "?crosswordId={crosswordId}",
            arguments = listOf(
                navArgument("crosswordId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            CrosswordScreen(
                dataViewModel = dataViewModel,
                settingsVm = settingsViewModel,
                crosswordId = it.arguments?.getString("crosswordId")
            ) {
                navController.popBackStack()
            }
        }
    }
}