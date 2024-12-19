package ru.tabakon.integrator.activities.main.ui.navigation

import androidx.navigation.NavHostController

class AppActions(
    private val navController: NavHostController,
    private val routes: AppDestinations
) {

    // Navigates to about screen from home options menu
    val navigateToSettings: () -> Unit = {
        navController.navigate(routes.SETTINGS)
    }

    // Navigates to previous screen from current screen.
    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}