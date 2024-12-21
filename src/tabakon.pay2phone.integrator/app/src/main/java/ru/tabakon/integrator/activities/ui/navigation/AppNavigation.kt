package ru.tabakon.integrator.activities.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.tabakon.integrator.MainApplication
import ru.tabakon.integrator.activities.ui.screens.home.HomeScreen
import ru.tabakon.integrator.activities.ui.screens.settings.SettingScreen
import ru.tabakon.integrator.activities.ui.screens.settings.SettingScreenViewModel

@Composable
fun AppNavigation(
    startDestination: String = AppDestinations.HOME_ROUTE,
    routes: AppDestinations = AppDestinations
) {
    // Create a NavHostController to handle navigation.
    val navController = rememberNavController()
    val actions = remember(navController) {
        AppActions(navController, routes)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(
            AppDestinations.HOME_ROUTE
        ) {
            HomeScreen(
                actions.navigateToSettings
            )
        }

        composable(
            AppDestinations.SETTINGS
        ) {
            val viewModel = SettingScreenViewModel(
                MainApplication.settingsStorage().getHost(),
                MainApplication.settingsStorage().getPort(),
            );

            SettingScreen(
                viewModel,
                actions.navigateUp);
        }

    }
}