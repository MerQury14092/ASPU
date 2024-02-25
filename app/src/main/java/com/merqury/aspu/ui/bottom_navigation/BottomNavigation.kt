package com.merqury.aspu.ui.bottom_navigation

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun NavBarPreview(){
    val navController = rememberNavController()
    BottomNavigation(navController = navController)
}
@Composable
fun BottomNavigation(
    navController: NavController
) {
    val listItems = listOf(
        BottomItem.News,
        BottomItem.Timetable,
        BottomItem.Other,
        BottomItem.Settings
    )
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        listItems.forEach {item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                          navController.navigate(item.route)
                },
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = "", modifier = Modifier.size(50.dp))
                }
            )
        }
    }
}