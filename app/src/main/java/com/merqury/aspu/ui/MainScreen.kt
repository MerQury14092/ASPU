package com.merqury.aspu.ui

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
import androidx.compose.material.ripple
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
import com.merqury.aspu.services.showTimetableWebPageView
import com.merqury.aspu.services.urlForCurrentFaculty
import com.merqury.aspu.ui.navfragments.news.NewsScreen
import com.merqury.aspu.ui.navfragments.other.OtherScreen
import com.merqury.aspu.ui.navfragments.settings.SettingsScreen
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.settings.toggleTheme
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import com.merqury.aspu.ui.theme.themeChangeDuration

val topBarContent: MutableState<@Composable () -> Unit> = mutableStateOf({})
val content: MutableState<@Composable () -> Unit> =
    mutableStateOf(getContentByRoute(settingsPreferences.getString("initial_route", "news")!!))
val onASPUButtonClick: MutableState<() -> Unit> = mutableStateOf({
    when (selected_page.value) {
        "news" -> {
            showWebPage(urlForCurrentFaculty(), "http")
        }
        "timetable" -> {
            showTimetableWebPageView()
        }
        "settings" -> toggleTheme()
        else -> showWebPage("agpu.net", "http")
    }
})

@Composable
fun MainScreen() {

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(.06f)
                        .fillMaxWidth()
                        .background(
                            animateColorAsState(
                                targetValue = theme.value[SurfaceTheme.foreground]!!,
                                animationSpec = tween(durationMillis = themeChangeDuration),
                                label = ""
                            ).value
                        )
                ) { topBarContent.value() }
                Divider(
                    color = animateColorAsState(
                        targetValue = theme.value[SurfaceTheme.divider]!!,
                        animationSpec = tween(durationMillis = themeChangeDuration),
                        label = ""
                    ).value,
                    modifier = Modifier.height(2.dp)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxHeight(.075f)
                    .fillMaxWidth()
                    .background(
                        animateColorAsState(
                            targetValue = theme.value[SurfaceTheme.foreground]!!,
                            animationSpec = tween(durationMillis = themeChangeDuration),
                            label = ""
                        ).value
                    )
            ) {
                Column {
                    Divider(
                        color = animateColorAsState(
                            targetValue = theme.value[SurfaceTheme.divider]!!,
                            animationSpec = tween(durationMillis = themeChangeDuration),
                            label = ""
                        ).value,
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
        NavBarItem(title = "Новости", icon = R.drawable.news_icon, "news")
        NavBarItem(
            title = "Расписание",
            icon = R.drawable.timetable_icon,
            "timetable"
        )
        Image(painter = painterResource(id = R.drawable.agpu_logo), contentDescription = null,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true
                )
            )
            {
                onASPUButtonClick.value()
            }
        )
        NavBarItem(
            title =
            when (settingsPreferences.getString("user", "student")) {
                "student" -> "Студенту"
                "teacher" -> "Педагогу"
                else -> "Кому?"
            }, icon = R.drawable.other_icon,
            "other"
        )
        NavBarItem(
            title = "Настройки",
            icon = R.drawable.settings_icon,
            "settings"
        )
    }
}

@Composable
fun NavBarItem(
    title: String,
    icon: Int,
    route: String
) {
    val selected = selected_page.value == route
    Box(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            content.value = getContentByRoute(route)
            selected_page.value = route

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
                        targetValue = if (selected)
                            theme.value[SurfaceTheme.enable]!!
                        else
                            theme.value[SurfaceTheme.disable]!!,
                        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
                        label = ""
                    ).value
                )
            )
            forNavBarUpdate.value
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color =
                animateColorAsState(
                    targetValue = if (selected)
                        theme.value[SurfaceTheme.enable]!!.copy(1f)
                    else
                        theme.value[SurfaceTheme.disable]!!.copy(
                            if (settingsPreferences.getBoolean("text_in_navbar", true))
                                1f
                            else
                                0f
                        ),
                    animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
                    label = ""
                ).value,
            )
        }
    }
}

fun getContentByRoute(route: String): @Composable () -> Unit {
    val news: @Composable () -> Unit = { NewsScreen(topBarContent) }
    val timetable: @Composable () -> Unit = { TimetableScreen(topBarContent) }
    val settings: @Composable () -> Unit = { SettingsScreen(topBarContent) }
    val other: @Composable () -> Unit = { OtherScreen(topBarContent) }
    return when (route) {
        "news" -> news
        "timetable" -> timetable
        "other" -> other
        else -> settings
    }
}