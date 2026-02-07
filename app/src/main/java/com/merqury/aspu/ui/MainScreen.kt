package com.merqury.aspu.ui

import android.annotation.SuppressLint
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.IntroShowcaseScope
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.news.NewsService.urlForCurrentFaculty
import com.merqury.aspu.services.timetable.showTimetableWebPageView
import com.merqury.aspu.ui.navfragments.news.NewsScreen
import com.merqury.aspu.ui.navfragments.other.OtherScreen
import com.merqury.aspu.ui.navfragments.profile.ProfileScreen
import com.merqury.aspu.ui.navfragments.settings.SettingsScreen
import com.merqury.aspu.ui.navfragments.settings.toggleTheme
import com.merqury.aspu.ui.navfragments.timetable.TimetableScreen
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.training.TrainingStates
import com.merqury.aspu.ui.training.hintTargetModifier


val topBarContent: MutableState<@Composable () -> Unit> = mutableStateOf({})
val onASPUButtonClick: MutableState<() -> Unit> = mutableStateOf({
    when (selected_page.value) {
        "news" -> {
            val inBrowser = AppSettings.useIncludedBrowser
            if (inBrowser)
                showWebPage(urlForCurrentFaculty(), "http")
            else
                openInBrowser(urlForCurrentFaculty(), "http")
        }

        "timetable" -> {
            showTimetableWebPageView()
        }

        "settings" -> toggleTheme()
        else -> {
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
//            shareTimetable()
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

@SuppressLint("StaticFieldLeak")
private var optionalNavController: NavHostController? = null
private inline val navController: NavHostController get() = optionalNavController!!
private val initialRoute = AppSettings.initialRoute

@Composable
fun MainScreen() {
    optionalNavController = rememberNavController()
    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(.06f)
                        .fillMaxWidth()
                        .background(SurfaceTheme.appBars.color)
                ) {
                    if (TrainingStates.isTraining)
                        topBarContent.value()
                    else
                        AnimatedContent(
                            targetState = topBarContent.value,
                            label = "",
                            transitionSpec = {
                                val direction = slideInDirection()
                                return@AnimatedContent slideInHorizontally(
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
            NavHost(navController = navController, startDestination = initialRoute) {
                animatedComposable("news") {
                    NewsScreen(header = topBarContent)
                }
                animatedComposable("timetable") {
                    TimetableScreen(header = topBarContent)
                }
                animatedComposable("other") {
                    OtherScreen(header = topBarContent)
                }
                animatedComposable("settings") {
                    SettingsScreen(header = topBarContent)
                }
                animatedComposable("account") {
                    ProfileScreen(header = topBarContent)
                }
            }
        }

    }
}

private fun NavGraphBuilder.animatedComposable(route: String, content: @Composable () -> Unit) {
    composable(
        route,
        enterTransition = {
            if (TrainingStates.isTraining)
                EnterTransition.None
            else
                slideInHorizontally(tween(400)) { slideInDirection() * it }
        },
        exitTransition = {
            if (TrainingStates.isTraining)
                ExitTransition.None
            else
                slideOutHorizontally(tween(400)) { (-slideInDirection()) * it }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}


var selected_page = mutableStateOf(AppSettings.initialRoute)

@Composable
fun NavigationBar() {
    val navBarItemWidth = LocalConfiguration.current.screenWidthDp.dp / 5
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            IntroShowcase(
                showIntroShowCase = TrainingStates.newsNavItem,
                onShowCaseCompleted = {
                    TrainingStates.newsNavItem = false
                    if (AppConfig.useNewsConfig().canUse)
                        TrainingStates.newsHeader = true
                    else
                        TrainingStates.news = true
                    routeTo("news")
                }) {
                NavBarItem(
                    title = "Новости",
                    icon = R.drawable.news_icon,
                    "news",
                    navBarItemWidth,
                    "Здесь вы можете ознакомиться с самыми свежими новостями ВУЗа"
                )
            }
            IntroShowcase(
                showIntroShowCase = TrainingStates.timetableNavItem,
                onShowCaseCompleted = {
                    TrainingStates.timetableNavItem = false
                    if (AppConfig.useTimetableConfig().canUse)
                        TrainingStates.timetableHeader = true
                    else
                        TrainingStates.timetable = true
                    routeTo("timetable")
                }) {
                NavBarItem(
                    title = "Расписание",
                    icon = R.drawable.timetable_icon,
                    "timetable",
                    navBarItemWidth,
                    "Здесь вы можете ознакомиться с актуальным расписанием"
                )
            }
            Spacer(modifier = Modifier.size(navBarItemWidth))
            IntroShowcase(
                showIntroShowCase = TrainingStates.otherNavItem,
                onShowCaseCompleted = {
                    TrainingStates.otherNavItem = false
                    TrainingStates.other = true
                    routeTo("other")
                }) {
                NavBarItem(
                    title =
                    when (AppSettings.whoIsUser) {
                        "student" -> "Студенту"
                        "teacher" -> "Педагогу"
                        else -> "Кому?"
                    }, icon = R.drawable.other_icon,
                    "other",
                    navBarItemWidth,
                    "Здесь расположены вкладки сайта и некоторая интересная функциональнотсь приложения"
                )
            }
            if (!AppSettings.eiosLogged)
                IntroShowcase(
                    showIntroShowCase = TrainingStates.settingsNavItem,
                    onShowCaseCompleted = {
                        TrainingStates.settingsNavItem = false
                        TrainingStates.settings = true
                        routeTo("settings")
                    }) {
                    NavBarItem(
                        title = "Настройки",
                        icon = R.drawable.settings_icon,
                        "settings",
                        navBarItemWidth,
                        "Здесь можно настроить приложение под себя"
                    )
                }
            else
                IntroShowcase(
                    showIntroShowCase = TrainingStates.accountNavItem,
                    onShowCaseCompleted = {
                        TrainingStates.accountNavItem = false
                        TrainingStates.accountHeader = true
                        routeTo("account")
                    }) {
                    NavBarItem(
                        title = "Профиль",
                        icon = R.drawable.profile,
                        "account",
                        navBarItemWidth,
                        "Это ваш мобильный аккаунт ЭИОС"
                    )
                }

        }
        if (TrainingStates.aspuButton)
            IntroShowcase(
                showIntroShowCase = true,
                onShowCaseCompleted = {
                    TrainingStates.aspuButton = false
                    TrainingStates.aspuButtonHintClosure()
                }) {
                AspuButton(
                    modifier = hintTargetModifier(
                        0,
                        "Функциональность кнопки",
                        TrainingStates.aspuButtonDescription
                    )
                )
            }
        else
            AspuButton()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AspuButton(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.agpu_logo),
                contentDescription = null,
                modifier = Modifier
                    .bounceClick(
                        onClick = {
                            onASPUButtonClick.value()
                        },
                        onLongClick = {
                            onASPUButtonLongClick.value()
                        }
                    )
                    .fillMaxHeight()
                    .then(modifier)
            )
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

private var lastClicked = 0L
fun routeTo(route: String) {
    if (selected_page.value == route)
        return
    requestQueue!!.cancelAll { true }
    lastRoute = selected_page.value
    selected_page.value = route
    navController.navigate(route)
}

@Composable
fun IntroShowcaseScope.NavBarItem(
    title: String,
    icon: Int,
    route: String,
    size: Dp,
    description: String
) {
    val selectedRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    if (selectedRoute != null && selected_page.value != selectedRoute) {
        lastRoute = selected_page.value
        selected_page.value = selectedRoute
    }
    val selected = selectedRoute == route
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
//                if (System.currentTimeMillis() - lastClicked > 500) {
                routeTo(route)
//                    lastClicked = System.currentTimeMillis()
//                }
            }
            .width(size)
            .then(hintTargetModifier(0, title, description)),
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