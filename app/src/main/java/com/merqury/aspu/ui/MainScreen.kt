package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.ui.navfragments.news.NewsScreen
import com.merqury.aspu.ui.navfragments.other.OtherScreen
import com.merqury.aspu.ui.navfragments.settings.SettingsScreen
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

val topBarContent: MutableState<@Composable () -> Unit> = mutableStateOf({})
val content: MutableState<@Composable () -> Unit> = mutableStateOf({
    when (settingsPreferences.getString("initial_route", "news")) {
        "news" -> NewsScreen(topBarContent)
        "timetable" -> TimetableScreen(topBarContent)
        "other" -> OtherScreen()
        "settings" -> SettingsScreen()
    }
})

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(.06f)
                        .fillMaxWidth()
                        .background(theme.value[SurfaceTheme.foreground]!!)
                ) { topBarContent.value() }
                Divider(
                    color = theme.value[SurfaceTheme.divider]!!,
                    modifier = Modifier.height(2.dp)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxHeight(.075f)
                    .fillMaxWidth()
                    .background(theme.value[SurfaceTheme.foreground]!!)
            ) {
                Column {
                    Divider(
                        color = theme.value[SurfaceTheme.divider]!!,
                        modifier = Modifier.height(2.dp)
                    )
                    NavigationBar()
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            content.value()
        }
    }
}

private val forNavBarUpdate = mutableStateOf(true)

fun navBarUpdate() {
    forNavBarUpdate.value = !forNavBarUpdate.value
}

var selected_page = mutableStateOf(settingsPreferences.getString("initial_route", "news")!!)

@Composable
fun NavigationBar() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        forNavBarUpdate.value
        NavBarItem(title = "Новости", icon = R.drawable.news_icon, selected_page.value == "news") {
            NewsScreen(topBarContent)
            selected_page.value = "news"
        }
        NavBarItem(title = "Расписание", icon = R.drawable.timetable_icon, selected_page.value == "timetable") {
            TimetableScreen(topBarContent)
            selected_page.value = "timetable"
        }
        Image(painter = painterResource(id = R.drawable.agpu_logo), contentDescription = null,
            modifier = Modifier.clickable {
                printlog("BUTTON CLICKED")
            })
        NavBarItem(
            title =
            when (settingsPreferences.getString("user", "student")) {
                "student" -> "Студенту"
                "teacher" -> "Педагогу"
                else -> "Кому?"
            }, icon = R.drawable.other_icon,
            selected_page.value == "other"
        ) {
            OtherScreen(/*topBarContent*/)
            selected_page.value = "other"
            topBarContent.value = {}
        }
        NavBarItem(title = "Настройки", icon = R.drawable.settings_icon, selected_page.value == "settings") {
            SettingsScreen(/*topBarContent*/)
            selected_page.value = "settings"
            topBarContent.value = {}
        }
    }
}

@Composable
fun NavBarItem(
    title: String,
    icon: Int,
    selected: Boolean,
    screen: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            content.value = screen

        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(25.dp)
                    .offset(
                        y = animateDpAsState(
                            targetValue = if (selected) -(5.dp) else 0.dp,
                            animationSpec = tween(
                                durationMillis = 100,
                                easing = FastOutSlowInEasing
                            ),
                            label = ""
                        ).value
                    ),
                colorFilter = ColorFilter.tint(
                    animateColorAsState(
                        targetValue = if(selected)
                        theme.value[SurfaceTheme.enable]!!
                    else
                        theme.value[SurfaceTheme.disable]!!,
                        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
                        label = ""
                    ).value
                )
            )
//            if(selected)
//                        theme.value[SurfaceTheme.enable]!!
//                    else
//                        theme.value[SurfaceTheme.disable]!!
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color =
                if(selected)
                    theme.value[SurfaceTheme.enable]!!
                else
                    theme.value[SurfaceTheme.disable]!!
            )
        }
    }
}