package com.merqury.aspu.ui.navfragments.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.merqury.aspu.appContext
import com.merqury.aspu.appVersion
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.cache
import com.merqury.aspu.services.sendToDevEmail
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.goToScreen
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.navfragments.news.newsLoaded
import com.merqury.aspu.ui.navfragments.news.showFacultySelectModalWindow
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.other.Terminal
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.isDarkThemeOn
import com.merqury.aspu.ui.theme.theme
import com.merqury.aspu.ui.theme.themeChangeDuration
import com.merqury.aspu.ui.theme.updateTheme
import java.util.concurrent.TimeUnit

val settingsPreferences = appContext?.getSharedPreferences("settings", Context.MODE_PRIVATE)!!
val selectableDisciplines =
    appContext?.getSharedPreferences("selectable_disciplines", Context.MODE_PRIVATE)!!


private val settingsUpdate = mutableStateOf(false)

fun reloadSettingsScreen() {
    settingsUpdate.value = !settingsUpdate.value
}

@Composable
fun SettingsScreen(header: MutableState<@Composable () -> Unit>) {
    header.value = { TitleHeader(title = "Настройки") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.background]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value
            )
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            settingsUpdate.value
            SettingsChapter(
                title = "Общие настройки",
                buttons = listOf(
                    ClickableSettingsButton(
                        "Кто использует приложение: ${
                            when (settingsPreferences.getString("user", "student")) {
                                "student" -> "студент"
                                "teacher" -> "преподаватель"
                                else -> settingsPreferences.getString("user", "Кто?")
                            }
                        }"
                    ) { selectUser() },
                    ClickableSettingsButton(
                        "Начальная вкладка при входе: ${
                            when (settingsPreferences.getString("initial_route", "news")) {
                                "news" -> "новости"
                                "timetable" -> "расписание"
                                "other" -> "студенту"
                                else -> "настройки"
                            }
                        }"
                    ) { selectInitialRoute() },
                    SwitchableSettingsPreferenceButton(
                        "Использовать встроенный браузер", "use_included_browser"
                    )
                )
            )
            SettingsChapter(
                title = "Новости и расписание",
                buttons = listOf(
                    ClickableSettingsButton(
                        "Выбранная категория новостей при входе: ${
                            NewsCategoryEnum.valueOf(
                                settingsPreferences.getString("news_category", "agpu")!!
                            ).localizedName
                        }"
                    ) {
                        showFacultySelectModalWindow {
                            settingsPreferences.edit().putString("news_category", it.name).apply()
                            reloadSettingsScreen()
                        }
                    },
                    ClickableSettingsButton(
                        "${
                            when (settingsPreferences.getString("user", "student")) {
                                "teacher" -> "Вы"
                                else -> "Выбранная группа"
                            }
                        }: ${settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")}"
                    ) {
                        showSelectIdModalWindow(
                            filteredBy = when (settingsPreferences.getString("user", "student")) {
                                "student" -> "group"
                                "teacher" -> "teacher"
                                else -> "group"
                            }
                        ) {
                            settingsPreferences.edit().putString("timetable_id", it.searchContent)
                                .apply()
                            settingsPreferences.edit()
                                .putString("timetable_id_owner", it.type.uppercase())
                                .apply()
                            selectableDisciplines.edit().clear().apply()
                            reloadSettingsScreen()
                        }
                    },
                    if (settingsPreferences.getString(
                            "user",
                            "student"
                        ) == "student"
                    ) SwitchableSettingsPreferenceButton(
                        "Фильтрация пар",
                        "filtration_on"
                    ) else null,
                    ClickableSettingsButton(
                        "Данные хранятся в кэше: ${
                            when (settingsPreferences.getLong(
                                "timeCache",
                                TimeUnit.HOURS.toSeconds(3)
                            )) {
                                0L -> "не хранятся"
                                TimeUnit.MINUTES.toSeconds(30) -> "пол часа"
                                TimeUnit.HOURS.toSeconds(1) -> "1 час"
                                TimeUnit.HOURS.toSeconds(3) -> "3 часа"
                                TimeUnit.HOURS.toSeconds(5) -> "5 часов"
                                TimeUnit.HOURS.toSeconds(12) -> "12 часов"
                                else -> "${
                                    settingsPreferences.getLong(
                                        "timeCache",
                                        TimeUnit.HOURS.toSeconds(3)
                                    )
                                } minutes"
                            }
                        }"
                    ) {
                        showSelectListDialog(mapOf(
                            "Отключить" to {
                                settingsPreferences.edit().putLong("timeCache", 0L).apply()
                                reloadSettingsScreen()
                            },
                            "Пол часа" to {
                                settingsPreferences.edit()
                                    .putLong("timeCache", TimeUnit.MINUTES.toSeconds(30)).apply()
                                reloadSettingsScreen()
                            },
                            "Час" to {
                                settingsPreferences.edit()
                                    .putLong("timeCache", TimeUnit.HOURS.toSeconds(1)).apply()
                                reloadSettingsScreen()
                            },
                            "3 часа" to {
                                settingsPreferences.edit()
                                    .putLong("timeCache", TimeUnit.HOURS.toSeconds(3)).apply()
                                reloadSettingsScreen()
                            },
                            "5 часов" to {
                                settingsPreferences.edit()
                                    .putLong("timeCache", TimeUnit.HOURS.toSeconds(5)).apply()
                                reloadSettingsScreen()
                            },
                            "12 часов" to {
                                settingsPreferences.edit()
                                    .putLong("timeCache", TimeUnit.HOURS.toSeconds(12)).apply()
                                reloadSettingsScreen()
                            }
                        ))
                    },
                    ClickableSettingsButton("Очистить кэш") {
                        cache.edit().clear().apply()
                        Toast.makeText(appContext!!, "Очищено!", Toast.LENGTH_LONG).show()
                        newsLoaded.value = false
                        timetableLoaded.value = false
                    }
                )
            )
            if (settingsPreferences.getBoolean("filtration_on", false)
                && settingsPreferences.getString("user", "student") == "student"
            ) {
                SettingsChapter(title = "Настройки фильтрации расписания", buttons = listOf(
                    ClickableSettingsButton(
                        "Выбранная подгруппа: ${
                            if (settingsPreferences.getInt(
                                    "selected_subgroup",
                                    0
                                ) == 0
                            )
                                "нет"
                            else
                                settingsPreferences.getInt(
                                    "selected_subgroup",
                                    0
                                ).toString()
                        }"
                    ) {
                        selectInitialSubgroup()
                    },
                    ClickableSettingsButton(
                        "Настроить политику показа дисциплин по выбору"
                    ) { showSelectableDisciplinesPreferences() },
                    ClickableSettingsButton(
                        "Очистить политику показа дисциплин по выбору"
                    ) {
                        selectableDisciplines.edit().clear().apply()
                        timetableLoaded.value = false
                        appContext!!.makeToast("Очищено!")
                    }
                ))
            }
            SettingsChapter(
                title = "Настройки внешнего вида", buttons = listOf(
                    ClickableSettingsButton(
                        "${
                            if (settingsPreferences.getString(
                                    "theme",
                                    if (appContext!!.isDarkThemeOn())
                                        "dark"
                                    else
                                        "light"
                                ) == "dark"
                            )
                                "Тёмная"
                            else
                                "Светлая"
                        } тема"
                    ) {
                        toggleTheme()
                    },
                    SwitchableSettingsPreferenceButton(
                        "Цветной фон ячеек в расписании",
                        "color_timetable"
                    ),
                    SwitchableSettingsPreferenceButton(
                        "Текст под иконками вкладок",
                        "text_in_navbar"
                    )
                )
            )
            Text(
                "О приложении", color = animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.text]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "Версия приложения: $appVersion",
                color = animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.text]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (appVersion!!.contains("alpha")) {
                Text(
                    "Приложение находится на этапе активной разработки и тестирования, в связи с этим в нём могут быть баги и ошибки",
                    color = animateColorAsState(
                        targetValue = theme.value[SurfaceTheme.text]!!,
                        animationSpec = tween(durationMillis = themeChangeDuration),
                        label = ""
                    ).value,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
            Text(
                "Если встретились с ошибкой, сообщите разработчику",
                color = animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.text]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
            Text(
                "petrakov.developer@gmail.com",
                color = Color.Blue,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        sendToDevEmail()
                    },
                textAlign = TextAlign.Left,
                textDecoration = TextDecoration.Underline

            )
            if (settingsPreferences.getBoolean("debug_mode", false))
                ClickableSettingsButton("Для разработчика") {
                    goToScreen(Terminal::class.java)
                }.getContent()()
        }
    }
}

fun toggleTheme() {
    if (settingsPreferences.getString(
            "theme",
            if (appContext!!.isDarkThemeOn())
                "dark"
            else
                "light"
        ) == "dark"
    )
        settingsPreferences.edit().putString("theme", "light").apply()
    else
        settingsPreferences.edit().putString("theme", "dark").apply()
    updateTheme()
    reloadSettingsScreen()
}

fun toggleBooleanSettingsPreference(name: String) {
    settingsPreferences
        .edit()
        .putBoolean(
            name,
            !settingsPreferences
                .getBoolean(name, false)
        )
        .apply()
//    appContext!!.makeToast(
//        settingsPreferences
//            .getBoolean(name, false).toString() + ": " + name
//    )
}

fun debugModeOn() {

}






