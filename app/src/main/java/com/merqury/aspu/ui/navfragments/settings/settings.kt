package com.merqury.aspu.ui.navfragments.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow

val settingsPreferences = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)!!

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
    val textSize = 16.sp
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text("Общие настройки")
        SettingsButton(
            onClick = {
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
                        }
                    )
                )
            }
        ) {
            settingsUpdate.value
            Text(
                text = "Кто использует приложение: ${
                    when (settingsPreferences.getString("user", "student")) {
                        "student" -> "студент"
                        "teacher" -> "преподаватель"
                        else -> "group"
                    }
                }", fontSize = textSize
            )
        }
        SettingsButton(onClick = {
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
        }) {
            settingsUpdate.value
            Text(
                text = "Начальная вкладка при входе: ${
                    when (settingsPreferences.getString("initial_route", "news")) {
                        "news" -> "новости"
                        "timetable" -> "расписание"
                        "other" -> "студенту"
                        else -> "настройки"
                    }
                }", fontSize = textSize
            )
        }
        Divider()
        Text("Новости и расписание")
        SettingsButton(
            onClick = {
                showFacultySelectModalWindow {
                    settingsPreferences.edit().putString("news_category", it.name).apply()
                    reloadSettingsScreen()
                }
            }
        ) {
            settingsUpdate.value
            Text(
                text = "Выбранная категория новостей при входе: ${
                    NewsCategoryEnum.valueOf(
                        settingsPreferences.getString("news_category", "agpu")!!
                    ).localizedName
                }", fontSize = textSize
            )
        }
        SettingsButton(
            onClick = {
                showSelectIdModalWindow(
                    filteredBy = when (settingsPreferences.getString("user", "student")) {
                        "student" -> "group"
                        "teacher" -> "teacher"
                        else -> "group"
                    }
                ) {
                    settingsPreferences.edit().putString("timetable_id", it.name).apply()
                    settingsPreferences.edit().putString("timetable_id_owner", it.owner.uppercase())
                        .apply()
                    reloadSettingsScreen()
                }
            }
        ) {
            settingsUpdate.value
            Text(
                text = "${
                    when (settingsPreferences.getString("user", "student")) {
                        "teacher" -> "Вы"
                        else -> "Выбранная группа"
                    }
                }: ${settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")}",
                fontSize = textSize
            )
        }
        SettingsButton(onClick = {
            showSimpleUpdatableModalWindow { _, update, forUpdate ->
                SettingsButton(onClick = {
                    showSelectListDialog(
                        mapOf(
                            "Нет" to {
                                settingsPreferences.edit().putInt("selected_subgroup", 0).apply()
                                update()
                            },
                            "1" to {
                                settingsPreferences.edit().putInt("selected_subgroup", 1).apply()
                                update()
                            },
                            "2" to {
                                settingsPreferences.edit().putInt("selected_subgroup", 2).apply()
                                update()
                            }
                        )
                    )
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
        }) {
            Text(text = "Настройки фильтрации расписания", fontSize = textSize)
        }
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
