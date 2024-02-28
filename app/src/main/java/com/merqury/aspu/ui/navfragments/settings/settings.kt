package com.merqury.aspu.ui.navfragments.settings

import android.content.Context
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
import com.merqury.aspu.context
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.ModalWindow
import com.merqury.aspu.ui.navfragments.news.FacultySelectModalWindow
import com.merqury.aspu.ui.navfragments.timetable.SelectIdModalWindowContent
import com.merqury.aspu.ui.showSimpleModalWindow

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
    ModalWindows()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text("Общие настройки")
        SettingsButton(
            onClick = {
                showSimpleModalWindow {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(.5f)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .clickable {
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
                                        it.value = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Студент")
                            }
                            Divider()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .clickable {
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
                                        it.value = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Преподаватель")
                            }
                        }
                    }
                }
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
                }"
            )
        }
        Divider()
        Text("Новости и расписание")
        SettingsButton(
            onClick = {
                selectNewsCategory.value = true
            }
        ) {
            settingsUpdate.value
            Text(
                text = "Выбранная категория новостей при входе: ${
                    NewsCategoryEnum.valueOf(
                        settingsPreferences.getString("news_category", "agpu")!!
                    ).localizedName
                }"
            )
        }
        SettingsButton(
            onClick = {

            }
        ) {
            settingsUpdate.value
            Text(
                text = "${
                    when (settingsPreferences.getString("user", "student")) {
                        "teacher" -> "Вы"
                        else -> "Выбранная группа"
                    }
                }: ${settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")}"
            )
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
        Box(modifier = Modifier.padding(5.dp)) {
            content()
        }
    }
}

private val selectIdModalWindowVisibility_settings = mutableStateOf(false)
private val selectNewsCategory = mutableStateOf(false)

@Composable
fun ModalWindows() {
    if (selectIdModalWindowVisibility_settings.value)
        ModalWindow {
            SelectIdModalWindowContent(
                selectIdModalWindowVisibility = selectIdModalWindowVisibility_settings,
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
    if (selectNewsCategory.value)
        FacultySelectModalWindow(
            facultySelectDialogVisible = selectNewsCategory,
            onSelectFaculty = {
                settingsPreferences.edit().putString("news_category", it.name).apply()
                reloadSettingsScreen()
            }
        )
}