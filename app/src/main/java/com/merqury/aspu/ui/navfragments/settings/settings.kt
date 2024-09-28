package com.merqury.aspu.ui.navfragments.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.canopas.lib.showcase.IntroShowcase
import com.merqury.aspu.appContext
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.intents.sendToDevEmail
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.goToScreen
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.navfragments.news.showFacultySelectModalWindow
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow
import com.merqury.aspu.ui.other.Terminal
import com.merqury.aspu.ui.routeTo
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.getThemeName
import com.merqury.aspu.ui.training.TrainingStates
import com.merqury.aspu.ui.training.hintTargetModifier

val selectableDisciplines =
    appContext?.getSharedPreferences("selectable_disciplines", Context.MODE_PRIVATE)!!


@Composable
fun SettingsScreen(header: MutableState<@Composable () -> Unit>) {
    if (TrainingStates.settings) {
        val onCompleted = {
            TrainingStates.aspuButtonDescription = "Открывает окно смены темы"
            TrainingStates.aspuButtonHintClosure = {
                TrainingStates.isTraining = false
            }
            TrainingStates.settings = false
            TrainingStates.aspuButton = true
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            IntroShowcase(
                showIntroShowCase = true,
                onShowCaseCompleted = onCompleted,
                dismissOnClickOutside = true
            ) {
                Box(
                    modifier = hintTargetModifier(
                        0,
                        "Настройка данного приложения",
                        "Здесь вы можете настроить приложение под себя: " +
                                "кто использует приложение, что показывать при входе, после входа, " +
                                "как это показывать и многое другое"
                    )
                )
            }
        }
    }
    val headerContent = @Composable { TitleHeader(title = "Настройки") }
    if (header.value != headerContent)
        header.value = headerContent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTheme.background.color)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            SettingsChapter(
                title = "Общие настройки",
                buttons = listOfNotNull(
                    if(!AppSettings.eiosLogged)
                    ClickableSettingsButton(
                        "Кто использует приложение: ${
                            when (val who = AppSettings.whoIsUser) {
                                "student" -> "студент"
                                "teacher" -> "преподаватель"
                                else -> who
                            }
                        }"
                    ) { selectUser() } else null,
                    ClickableSettingsButton(
                        "Начальная вкладка при входе: ${
                            when (AppSettings.initialRoute) {
                                "news" -> "новости"
                                "timetable" -> "расписание"
                                "other" -> "студенту"
                                "account" -> "аккаунт ЭИОС"
                                else -> "настройки"
                            }
                        }"
                    ) { selectInitialRoute() },
                    SwitchableSettingsPreferenceButton(
                        "Использовать встроенный браузер",
                        AppSettings.useIncludedBrowser
                    ) { AppSettings.useIncludedBrowser = it },
//                    ClickableSettingsButton(
//                        "Пройти заново вводный курс по интерфейсу приложения"
//                    ) {
//                        routeTo("news")
//                        TrainingStates.isTraining = true
//                        TrainingStates.newsNavItem = true
//                    }
                )
            )
            SettingsChapter(
                title = "Новости и расписание",
                buttons = listOfNotNull(
                    if (AppConfig.useNewsConfig().canUse)
                        ClickableSettingsButton(
                            "Выбранная категория новостей при входе: ${
                                NewsCategoryEnum.valueOf(AppSettings.newsCategory).localizedName
                            }"
                        ) {
                            showFacultySelectModalWindow {
                                AppSettings.newsCategory = it.name
                            }
                        } else null,
                    if(!AppSettings.eiosLogged)
                    ClickableSettingsButton(
                        "${
                            when (AppSettings.whoIsUser) {
                                "teacher" -> "Вы"
                                else -> "Выбранная группа"
                            }
                        }: ${AppSettings.timetableId}"
                    ) {
                        showSelectIdModalWindow(
                            timetableId = AppSettings.timetableId,
                            filteredBy = when (AppSettings.whoIsUser) {
                                "student" -> "group"
                                "teacher" -> "teacher"
                                else -> "group"
                            }
                        ) {
                            AppSettings.timetableId = it.searchContent
                            AppSettings.timetableIdOwner = it.type.uppercase()
                            selectableDisciplines.edit().clear().apply()
                        }
                    } else null,
                    if (AppSettings.whoIsUser == "student" && AppConfig.useTimetableConfig().canUse)
                        SwitchableSettingsPreferenceButton(
                            "Фильтрация пар",
                            AppSettings.timetableFiltration
                        ) { AppSettings.timetableFiltration = it } else null,
//                    ClickableSettingsButton(
//                        "Данные хранятся в кэше: ${
//                            when (AppSettings.timeCache) {
//                                0L -> "не хранятся"
//                                TimeUnit.MINUTES.toSeconds(30) -> "пол часа"
//                                TimeUnit.HOURS.toSeconds(1) -> "1 час"
//                                TimeUnit.HOURS.toSeconds(3) -> "3 часа"
//                                TimeUnit.HOURS.toSeconds(5) -> "5 часов"
//                                TimeUnit.HOURS.toSeconds(12) -> "12 часов"
//                                else -> "${AppSettings.timeCache} minutes"
//                            }
//                        }"
//                    ) {
//                        showSelectListDialog(mapOf(
//                            "Отключить" to {
//                                AppSettings.timeCache = 0
//                            },
//                            "Пол часа" to {
//                                AppSettings.timeCache = TimeUnit.MINUTES.toSeconds(30)
//                            },
//                            "Час" to {
//                                AppSettings.timeCache = TimeUnit.HOURS.toSeconds(1)
//                            },
//                            "3 часа" to {
//                                AppSettings.timeCache = TimeUnit.HOURS.toSeconds(3)
//                            },
//                            "5 часов" to {
//                                AppSettings.timeCache = TimeUnit.HOURS.toSeconds(5)
//                            },
//                            "12 часов" to {
//                                AppSettings.timeCache = TimeUnit.HOURS.toSeconds(12)
//                            }
//                        ))
//                    },
                    ClickableSettingsButton("Очистить кэш") {
                        appContext!!.getSharedPreferences("news-cache", Context.MODE_PRIVATE).edit()
                            .clear().apply()
                        Toast.makeText(appContext!!, "Очищено!", Toast.LENGTH_LONG).show()
                    }
                )
            )
            if (AppSettings.timetableFiltration
                && AppSettings.whoIsUser == "student"
                && AppConfig.useTimetableConfig().canUse
            ) {
                SettingsChapter(title = "Настройки фильтрации расписания", buttons = listOf(
                    ClickableSettingsButton(
                        "Выбранная подгруппа: ${
                            if (AppSettings.selectedSubgroup == 0)
                                "нет"
                            else
                                AppSettings.selectedSubgroup.toString()
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
                        appContext!!.makeToast("Очищено!")
                    }
                ))
            }
            SettingsChapter(
                title = "Настройки внешнего вида", buttons = listOf(
                    ClickableSettingsButton(
                        "${getThemeName(AppSettings.selectedTheme)} тема"
                    ) {
                        showSelectTheme()
                    },
                    SwitchableSettingsPreferenceButton(
                        "Цветной фон ячеек в расписании",
                        AppSettings.colorTimetable
                    ) { AppSettings.colorTimetable = it },
                    SwitchableSettingsPreferenceButton(
                        "Текст под иконками вкладок",
                        AppSettings.textInNavbar
                    ) { AppSettings.textInNavbar = it },
//                    ClickableSettingsButton(
//                        "Пользовательский экран загрузки"
//                    ) {
//
//                    }
                )
            )
            if(AppSettings.eiosLogged) {
                SettingsChapter(
                    title = "Настрйоки ЭИОС",
                    buttons = listOf(
                        ClickableSettingsButton(
                            "Выйти из аккаунта"
                        ) {
                            AppSettings.eiosLogged = false
                            routeTo("settings")
                        }
                    )
                )
            }
            Text(
                "О приложении", color = SurfaceTheme.text.color,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
//            Text(
//                "Версия приложения: $appVersion",
//                color = SurfaceTheme.text.color,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Left
//            )
//            Spacer(modifier = Modifier.height(5.dp))
//            if (appVersion!!.contains("alpha")) {
//                Text(
//                    "Приложение находится на этапе активной разработки и тестирования, в связи с этим в нём могут быть баги и ошибки",
//                    color = SurfaceTheme.text.color,
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Left
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//            }
            Text(
                "Версия приложения: ${appContext!!
                    .packageManager
                    .getPackageInfo(
                        appContext!!.packageName,
                        0
                    ).versionName!!}",
                color = SurfaceTheme.text.color,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
            Text(
                "Если встретились с ошибкой, сообщите разработчику",
                color = SurfaceTheme.text.color,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
            Text(
                "petrakov.developer@gmail.com",
                color = Color.Blue,
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick {
                        sendToDevEmail()
                    },
                textAlign = TextAlign.Left,
                textDecoration = TextDecoration.Underline

            )
            if (AppSettings.debugMode)
                ClickableSettingsButton("Для разработчика") {
                    goToScreen(Terminal::class.java)
                }.getContent()()
        }
    }
}

fun showSelectTheme() {
    showSelectListDialog(
        mapOf(
            "Светлая тема" to { setTheme("light") },
            "Тёмная тема" to { setTheme("dark") },
            "Морская тема" to { setTheme("sea") },
//                            "Лазурная тема" to { setTheme("site") }
        )
    )
}

fun toggleTheme() {
    showSelectTheme()
}

fun setTheme(name: String) {
    AppSettings.selectedTheme = name
}

fun getDefault(name: String): Boolean {
    return when (name) {
        "use_included_browser" -> true
        "text_in_navbar" -> true
        "color_timetable" -> true
        else -> false
    }
}





