package com.merqury.aspu.ui.navfragments.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import com.merqury.aspu.context
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.navfragments.news.showFacultySelectModalWindow
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow

val settingsPreferences = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)!!
val selectableDisciplines = context?.getSharedPreferences("selectable_disciplines", Context.MODE_PRIVATE)!!

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}

private val settingsUpdate = mutableStateOf(false)

fun reloadSettingsScreen() {
    settingsUpdate.value = !settingsUpdate.value
}

@Composable
fun SettingsScreen() {
    Column {
        settingsUpdate.value
        SettingsChapter(
            title = "Общие настройки",
            buttons = mapOf(
                "Кто использует приложение: ${
                    when (settingsPreferences.getString("user", "student")) {
                        "student" -> "студент"
                        "teacher" -> "преподаватель"
                        else -> "group"
                    }
                }" to { selectUser() },
                "Начальная вкладка при входе: ${
                    when (settingsPreferences.getString("initial_route", "news")) {
                        "news" -> "новости"
                        "timetable" -> "расписание"
                        "other" -> "студенту"
                        else -> "настройки"
                    }
                }" to { selectInitialRoute() }
            )
        )
        SettingsChapter(
            title = "Новости и расписание",
            buttons = mapOf(
                "Выбранная категория новостей при входе: ${
                    NewsCategoryEnum.valueOf(
                        settingsPreferences.getString("news_category", "agpu")!!
                    ).localizedName
                }" to {
                    showFacultySelectModalWindow {
                        settingsPreferences.edit().putString("news_category", it.name).apply()
                        reloadSettingsScreen()
                    }
                },
                "${
                    when (settingsPreferences.getString("user", "student")) {
                        "teacher" -> "Вы"
                        else -> "Выбранная группа"
                    }
                }: ${settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")}" to {
                    showSelectIdModalWindow(
                        filteredBy = when (settingsPreferences.getString("user", "student")) {
                            "student" -> "group"
                            "teacher" -> "teacher"
                            else -> "group"
                        }
                    ) {
                        settingsPreferences.edit().putString("timetable_id", it.name).apply()
                        settingsPreferences.edit()
                            .putString("timetable_id_owner", it.owner.uppercase())
                            .apply()
                        selectableDisciplines.edit().clear().apply()
                        reloadSettingsScreen()
                    }
                },
                if (settingsPreferences.getString(
                        "user",
                        "student"
                    ) == "student"
                ) "Настройки фильтрации расписания" to { filterSettings() } else "" to {}
            )
        )
    }
}






