package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.merqury.aspu.ui.bottom_navigation.BottomNavigation
import com.merqury.aspu.ui.bottom_navigation.NavGraph

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    MainScreen()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(){
    val navController = rememberNavController()
    Column(modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)){
            NavGraph(navHostController = navController)
        }
        BottomNavigation(navController = navController)
    }
}