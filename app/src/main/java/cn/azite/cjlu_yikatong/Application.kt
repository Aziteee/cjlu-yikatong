package cn.azite.cjlu_yikatong

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.azite.cjlu_yikatong.ui.MainDestinations
import cn.azite.cjlu_yikatong.ui.screen.HomeScreen
import cn.azite.cjlu_yikatong.ui.screen.LoginScreen

@Composable
fun Application() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MainDestinations.HOME_ROUTE
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            HomeScreen(navController)
        }
        composable(MainDestinations.LOGIN_ROUTE) {
            LoginScreen(navController)
        }
    }
}