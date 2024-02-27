package com.merqury.aspu.ui.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.merqury.aspu.ui.navfragments.OtherScreen
import com.merqury.aspu.ui.navfragments.SettingsScreen
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen
import com.merqury.aspu.ui.navfragments.news.NewsScreen

@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = "timetable"){
        composable("news"){
            NewsScreen()
        }
        composable("timetable"){
            TimetableScreen()
        }
        composable("other"){
            OtherScreen()
        }
        composable("settings"){
            SettingsScreen()
        }
    }
}