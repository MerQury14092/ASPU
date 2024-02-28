package com.merqury.aspu.ui.navfragments.settings

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.context
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.navfragments.news.showFacultySelectModalWindow
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow

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

fun filterSettings() {
    showSimpleUpdatableModalWindow { _, update, forUpdate ->
        forUpdate.value
        FilterSwitcher(update, forUpdate)
        if (settingsPreferences.getBoolean("filtration_on", false)) {
            SelectInitialSubgroupButton(update, forUpdate)
            ShowSelectableDisciplinesPreferencesButton()
            ClearSelectableDisciplinesPolicy()
        }
    }
}

@Composable
fun FilterSwitcher(update: () -> Unit, forUpdate: MutableState<Boolean>) {
    SettingsButton(onClick = {
        settingsPreferences
            .edit()
            .putBoolean(
                "filtration_on",
                !settingsPreferences
                    .getBoolean("filtration_on", false)
            )
            .apply()
        update()
        timetableLoaded.value = false
    }) {
        forUpdate.value
        Text(
            text =
            if (settingsPreferences.getBoolean("filtration_on", false))
                "Выключить фильтрацию пар"
            else
                "Включить фильтрацию пар"
        )
    }
}

@Composable
fun SelectInitialSubgroupButton(update: () -> Unit, forUpdate: MutableState<Boolean>) {
    SettingsButton(onClick = {
        selectInitialSubgroup(update)
    }) {
        forUpdate.value
        val selectedSubgroup = settingsPreferences.getInt(
            "selected_subgroup",
            0
        )
        Text(
            text = "Выбранная подгруппа: ${
                if (selectedSubgroup == 0)
                    "нет"
                else
                    selectedSubgroup.toString()
            }"
        )
    }
}

@Composable
fun ShowSelectableDisciplinesPreferencesButton() {
    SettingsButton(onClick = {
        showSelectableDisciplinesPreferences()
    }) {
        Text(text = "Показать политику показа дисциплин по выбору")
    }
}

@Composable
fun ClearSelectableDisciplinesPolicy(){
    SettingsButton(onClick = {
        selectableDisciplines.edit().clear().apply()
        timetableLoaded.value = false
    }) {
        Text(text = "Очистить политику показа дисциплин по выбору")
    }
}

fun showSelectableDisciplinesPreferences() {
    showSimpleUpdatableModalWindow { _, update, forUpdate ->
        Box(modifier = Modifier.padding(10.dp)){
            Column {
                if (selectableDisciplines.all.isEmpty())
                    Text(text = "Пусто")
                else
                    selectableDisciplines.all.forEach {
                        SettingsButton(onClick = {
                            selectableDisciplines
                                .edit()
                                .putBoolean(it.key, !selectableDisciplines.getBoolean(it.key, true))
                                .apply()
                            update()
                            timetableLoaded.value = false
                        }) {
                            forUpdate.value
                            Text(text = "${it.key}: ${if(selectableDisciplines.getBoolean(it.key, true)) "Показывать" else "Не показывать"}")
                        }
                    }
            }
        }
    }
}

fun selectInitialSubgroup(update: () -> Unit) {
    showSelectListDialog(
        mapOf(
            "Нет" to {
                settingsPreferences.edit().putInt("selected_subgroup", 0).apply()
                update()
                timetableLoaded.value = false
            },
            "1" to {
                settingsPreferences.edit().putInt("selected_subgroup", 1).apply()
                update()
                timetableLoaded.value = false
            },
            "2" to {
                settingsPreferences.edit().putInt("selected_subgroup", 2).apply()
                update()
                timetableLoaded.value = false
            }
        )
    )
}

fun selectInitialRoute() {
    showSelectListDialog(
        mapOf(
            "Новости" to {
                settingsPreferences.edit().putString("initial_route", "news").apply()
                reloadSettingsScreen()
            },
            "Расписание" to {
                settingsPreferences.edit().putString("initial_route", "timetable").apply()
                reloadSettingsScreen()
            },
            "Студенту" to {
                settingsPreferences.edit().putString("initial_route", "other").apply()
                reloadSettingsScreen()
            },
            "Настройки" to {
                settingsPreferences.edit().putString("initial_route", "settings").apply()
                reloadSettingsScreen()
            }
        )
    )
}

fun selectUser() {
    showSelectListDialog(
        mapOf(
            "Студент" to {
                settingsPreferences
                    .edit()
                    .putString("user", "student")
                    .apply()
                settingsPreferences
                    .edit()
                    .putString("timetable_id", "ВМ-ИВТ-2-1")
                    .apply()
                settingsPreferences
                    .edit()
                    .putString("timetable_id_owner", "GROUP")
                    .apply()
                reloadSettingsScreen()
                selectableDisciplines.edit().clear().apply()
            },
            "Преподаватель" to {
                settingsPreferences
                    .edit()
                    .putString("user", "teacher")
                    .apply()
                settingsPreferences
                    .edit()
                    .putString("timetable_id", "Лапшин Н.А.,ст.пр. ")
                    .apply()
                settingsPreferences
                    .edit()
                    .putString("timetable_id_owner", "TEACHER")
                    .apply()
                reloadSettingsScreen()
                selectableDisciplines.edit().clear().apply()
            }
        )
    )
}

@Composable
fun SettingsChapter(title: String, buttons: Map<String, () -> Unit>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title)
        buttons.entries.forEach {
            if (it.key.isNotEmpty())
                SettingsButton(onClick = it.value) {
                    Text(text = it.key, fontSize = 16.sp)
                }
        }
        Divider()
    }
}


@Composable
fun SettingsButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                onClick()
            }
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            content()
        }
    }
}
