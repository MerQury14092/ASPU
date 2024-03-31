package com.merqury.aspu.ui

import android.os.Vibrator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.core.content.ContextCompat.getSystemService
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.services.news.urlForCurrentFaculty
import com.merqury.aspu.services.timetable.showTimetableWebPageView
import com.merqury.aspu.ui.navfragments.news.NewsScreen
import com.merqury.aspu.ui.navfragments.news.newsLoaded
import com.merqury.aspu.ui.navfragments.other.OtherScreen
import com.merqury.aspu.ui.navfragments.profile.ProfileScreen
import com.merqury.aspu.ui.navfragments.settings.SettingsScreen
import com.merqury.aspu.ui.navfragments.settings.reloadSettingsScreen
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.settings.toggleBooleanSettingsPreference
import com.merqury.aspu.ui.navfragments.settings.toggleTheme
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color


val topBarContent: MutableState<@Composable () -> Unit> = mutableStateOf({})
val content: MutableState<@Composable () -> Unit> =
    mutableStateOf(getContentByRoute(settingsPreferences.getString("initial_route", "news")!!))
val onASPUButtonClick: MutableState<() -> Unit> = mutableStateOf({
    when (selected_page.value) {
        "news" -> {
            aspuButtonLoading.value = true
            val inBrowser = settingsPreferences.getBoolean("use_included_browser", true)
            if (inBrowser)
                showWebPage(urlForCurrentFaculty(), "http")
            else
                openInBrowser(urlForCurrentFaculty(), "http")
        }

        "timetable" -> {
            aspuButtonLoading.value = true
            showTimetableWebPageView()
        }

        "settings" -> toggleTheme()
        else -> {
            aspuButtonLoading.value = true
            val inBrowser = settingsPreferences.getBoolean("use_included_browser", true)
            if (inBrowser)
                showWebPage("agpu.net", "http")
            else
                openInBrowser("agpu.net", "http")
        }
    }
})
val magicState = mutableIntStateOf(3)
val onASPUButtonLongClick: MutableState<() -> Unit> = mutableStateOf({
    when (selected_page.value) {
        "news" -> {
            newsLoaded.value = false
        }

        "timetable" -> {
            timetableLoaded.value = false
        }

        "settings" -> {
            val v = getSystemService(appContext!!, Vibrator::class.java)!!
            if (magicState.intValue == 0 && !settingsPreferences.getBoolean("debug_mode", false)) {
                toggleBooleanSettingsPreference("debug_mode")
                printlog("Если хотите отключить это, пропишите debug off")
                reloadSettingsScreen()
                appContext!!.makeToast("DEBUG MODE ON")
                v.vibrate(100)
            }
            if (magicState.intValue > 0 && !settingsPreferences.getBoolean("debug_mode", false)) {
                v.vibrate(100)
                magicState.intValue--
            }
        }
    }
})
val aspuButtonLoading = mutableStateOf(false)


@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(.06f)
                        .fillMaxWidth()
                        .background(SurfaceTheme.appBars.color)
                ) { topBarContent.value() }
                Divider(
                    color = SurfaceTheme.divider.color,
                    modifier = Modifier.height(2.dp)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxHeight(.075f)
                    .fillMaxWidth()
                    .background(SurfaceTheme.appBars.color)
            ) {
                Column {
                    Divider(
                        color = SurfaceTheme.divider.color,
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

@OptIn(ExperimentalFoundationApi::class)
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
        Box(
            Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.agpu_logo), contentDescription = null,
                modifier = Modifier
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            onASPUButtonClick.value()
                        },
                        onLongClick = {
                            onASPUButtonLongClick.value()
                        }
                    )
                    .fillMaxHeight(
                        animateFloatAsState(
                            targetValue =
                            if (aspuButtonLoading.value) .8f else 1f,
                            label = "",
                            animationSpec = tween(durationMillis = 100)
                        ).value
                    )
            )
        }
        NavBarItem(
            title =
            when (settingsPreferences.getString("user", "student")) {
                "student" -> "Студенту"
                "teacher" -> "Педагогу"
                else -> "Кому?"
            }, icon = R.drawable.other_icon,
            "other"
        )
        if (!settingsPreferences.getBoolean("eios_logged", false))
            NavBarItem(
                title = "Настройки",
                icon = R.drawable.settings_icon,
                "settings"
            )
        else
            NavBarItem(
                title = "Профиль",
                icon = R.drawable.profile,
                "account",
            )
    }
}

fun routeTo(route: String) {
    content.value = getContentByRoute(route)
    selected_page.value = route
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
            routeTo(route)
        }
    ) {
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
                    if (selected)
                        SurfaceTheme.enable.color
                    else
                        SurfaceTheme.disable.color
                )
            )

            forNavBarUpdate.value
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected)
                    SurfaceTheme.enable.color.copy(1f)
                else
                    SurfaceTheme.disable.color.copy(
                        if (settingsPreferences.getBoolean("text_in_navbar", true))
                            1f
                        else
                            0f
                    )
            )
        }
    }
}

fun getContentByRoute(route: String): @Composable () -> Unit {
    val news: @Composable () -> Unit = { NewsScreen(topBarContent) }
    val timetable: @Composable () -> Unit = { TimetableScreen(topBarContent) }
    val settings: @Composable () -> Unit = { SettingsScreen(topBarContent) }
    val other: @Composable () -> Unit = { OtherScreen(topBarContent) }
    val profile: @Composable () -> Unit = { ProfileScreen(topBarContent) }
    return when (route) {
        "news" -> news
        "timetable" -> timetable
        "other" -> other
        "account" -> profile
        else -> settings
    }
}