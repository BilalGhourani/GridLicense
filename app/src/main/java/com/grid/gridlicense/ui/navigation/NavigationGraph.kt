package com.grid.gridlicense.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grid.gridlicense.ui.license.LicenseView
import com.grid.gridlicense.ActivityScopedViewModel
import com.grid.gridlicense.ui.home.HomeView
import com.grid.gridlicense.ui.login.LoginView

@Composable
fun AuthNavGraph(
        navController: NavHostController,
        startDestination: String,
        activityViewModel: ActivityScopedViewModel,
        modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.background(color = Color.White)
    ) {

        composable(route = "LoginView") {
            LoginView(
                navController = navController,
                activityScopedViewModel = activityViewModel
            )
        }

        composable(route = "HomeView") {
            HomeView(
                navController = navController,
                activityViewModel = activityViewModel
            )
        }

        composable(route = "LicenseView") {
            LicenseView(
                navController = navController,
                activityViewModel = activityViewModel
            )
        }
    }
}