package com.merqury.aspu.ui

import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.news.urlForCurrentFaculty
import com.merqury.aspu.ui.navfragments.news.NewsScreen
import com.merqury.aspu.ui.navfragments.other.OtherScreen
import com.merqury.aspu.ui.navfragments.profile.ProfileScreen
import com.merqury.aspu.ui.navfragments.settings.SettingsScreen
import com.merqury.aspu.ui.navfragments.settings.toggleTheme
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color


val topBarContent: MutableState<@Composable () -> Unit> = mutableStateOf({})
val content: MutableState<@Composable () -> Unit> =
    mutableStateOf(getContentByRoute(AppSettings.initialRoute))
val onASPUButtonClick: MutableState<() -> Unit> = mutableStateOf({
    when (selected_page.value) {
        "news" -> {
            aspuButtonLoading.value = true
            val inBrowser = AppSettings.useIncludedBrowser
            if (inBrowser)
                showWebPage(urlForCurrentFaculty(), "http")
            else
                openInBrowser(urlForCurrentFaculty(), "http")
        }

        "timetable" -> {
            aspuButtonLoading.value = true
//            showTimetableWebPageView() TODO
        }

        "settings" -> toggleTheme()
        else -> {
            aspuButtonLoading.value = true
            val inBrowser = AppSettings.useIncludedBrowser
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
        }

        "timetable" -> {
        }

        "settings" -> {
            val v = getSystemService(appContext!!, Vibrator::class.java)!!
            if (magicState.intValue == 0 && !AppSettings.debugMode) {
                AppSettings.debugMode = !AppSettings.debugMode
                printlog("Если хотите отключить это, пропишите debug off")
                appContext!!.makeToast("DEBUG MODE ON")
                v.vibrate(100)
            }
            if (magicState.intValue > 0 && !AppSettings.debugMode) {
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
                ) {
                    AnimatedContent(
                        targetState = topBarContent.value,
                        label = "",
                        transitionSpec = {
                            val direction = slideInDirection()
                            slideInHorizontally(
                                animationSpec = tween(durationMillis = 400)
                            ) { (direction) * it } togetherWith slideOutHorizontally(
                                animationSpec = tween(durationMillis = 400)
                            ) { (-direction) * it }
                        }
                    ) { content ->
                        content()
                    }
                }
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(SurfaceTheme.background.color)
        ) {
            AnimatedContent(
                targetState = selected_page.value,
                label = "",
                transitionSpec = {
                    val direction = slideInDirection()
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 400)
                    ) { (direction) * it } togetherWith slideOutHorizontally(
                        animationSpec = tween(durationMillis = 400)
                    ) { (-direction) * it }
                }
            ) { page ->
                getContentByRoute(page)()
            }
        }

    }
}

private var lastRoute = AppSettings.initialRoute
private fun slideInDirection(): Int { // 1 - справа налево; -1 слева направо
    val route = selected_page.value

    if (route == "settings" || route == "account")
        return 1

    if (route == "news")
        return -1

    if (route == "timetable") {
        if (lastRoute == "news")
            return 1
        return -1
    }
    if (route == "other") {
        if (lastRoute == "settings" || lastRoute == "account")
            return -1
        return 1
    }
    return -1
}

private val forNavBarUpdate = mutableStateOf(true)


var selected_page = mutableStateOf(AppSettings.initialRoute)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavigationBar() {
    val navBarItemWidth = LocalConfiguration.current.screenWidthDp.dp / 5
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            forNavBarUpdate.value
            NavBarItem(title = "Новости", icon = R.drawable.news_icon, "news", navBarItemWidth)
            NavBarItem(
                title = "Расписание",
                icon = R.drawable.timetable_icon,
                "timetable",
                navBarItemWidth
            )
            Spacer(modifier = Modifier.size(navBarItemWidth))
            NavBarItem(
                title =
                when (AppSettings.whoIsUser) {
                    "student" -> "Студенту"
                    "teacher" -> "Педагогу"
                    else -> "Кому?"
                }, icon = R.drawable.other_icon,
                "other",
                navBarItemWidth
            )
            if (!AppSettings.eiosLogged)
                NavBarItem(
                    title = "Настройки",
                    icon = R.drawable.settings_icon,
                    "settings",
                    navBarItemWidth
                )
            else
                NavBarItem(
                    title = "Профиль",
                    icon = R.drawable.profile,
                    "account",
                    navBarItemWidth
                )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = R.drawable.agpu_logo),
                    contentDescription = null,
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
        }
    }
}

fun routeTo(route: String) {
    requestQueue!!.cancelAll { true }
    lastRoute = selected_page.value
    selected_page.value = route
}

@Composable
fun NavBarItem(
    title: String,
    icon: Int,
    route: String,
    size: Dp
) {
    val selected = selected_page.value == route
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                routeTo(route)
            }
            .width(size),
        contentAlignment = Alignment.Center
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
                        if (AppSettings.textInNavbar)
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