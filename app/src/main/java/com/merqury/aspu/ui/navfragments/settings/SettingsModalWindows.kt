package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow

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