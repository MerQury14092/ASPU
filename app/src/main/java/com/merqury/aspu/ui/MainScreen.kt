package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.merqury.aspu.R
import com.merqury.aspu.enums.TimetableDisciplineType
import com.merqury.aspu.ui.navfragments.news.NewsScreen
import com.merqury.aspu.ui.navfragments.other.OtherScreen
import com.merqury.aspu.ui.navfragments.settings.SettingsScreen
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen

@Composable
fun MainScreenPreview(){
    Box(modifier = Modifier.fillMaxSize()){
        MainScreen()
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(){
    val topBarContent: MutableState<@Composable () -> Unit> = remember {
        mutableStateOf({})
    }
    val content: MutableState<@Composable () -> Unit> = remember {
        mutableStateOf({NewsScreen(topBarContent)})
    }
    Scaffold(
        topBar = {
            Box (modifier = Modifier
                .fillMaxHeight(.08f)
                .fillMaxWidth()
                .background(Color.Gray)){
                topBarContent.value()
            }
        },
        bottomBar = {
            Box (modifier = Modifier
                .fillMaxHeight(.085f)
                .fillMaxWidth()
                .background(Color.Gray)){
                NavigationBar(content, topBarContent)
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)){
            content.value()
        }
    }
}

@Composable
fun NavigationBar(
    content: MutableState<@Composable () -> Unit>,
    topBarContent: MutableState<@Composable () -> Unit>,
){
    TimetableDisciplineType::class.java
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(painter = painterResource(id = R.drawable.news_icon), contentDescription = null,
            modifier = Modifier.clickable {
                content.value = {NewsScreen(topBarContent)}
            }
        )
        Image(painter = painterResource(id = R.drawable.timetable_icon), contentDescription = null,
            modifier = Modifier.clickable {
                content.value = { TimetableScreen(topBarContent)}
            }
        )
        Image(painter = painterResource(id = R.drawable.other_icon), contentDescription = null,
            modifier = Modifier.clickable {
                content.value = { OtherScreen(/*topBarContent*/) }
                topBarContent.value = {}
            }
        )
        Image(painter = painterResource(id = R.drawable.settings_icon), contentDescription = null,
            modifier = Modifier.clickable {
                content.value = { SettingsScreen(/*topBarContent*/) }
                topBarContent.value = {}
            }
        )
    }
}