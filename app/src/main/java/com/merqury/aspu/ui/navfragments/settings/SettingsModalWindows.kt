package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.merqury.aspu.ui.navBarUpdate
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.showSelectListDialog

fun selectInitialSubgroup() {
    showSelectListDialog(
        mapOf(
            "Нет" to {
                settingsPreferences.edit().putInt("selected_subgroup", 0).apply()
                reloadSettingsScreen()
                timetableLoaded.value = false
            },
            "1" to {
                settingsPreferences.edit().putInt("selected_subgroup", 1).apply()
                reloadSettingsScreen()
                timetableLoaded.value = false
            },
            "2" to {
                settingsPreferences.edit().putInt("selected_subgroup", 2).apply()
                reloadSettingsScreen()
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
            if(settingsPreferences.getBoolean("eios_logged", false))
                "Аккаунт ЭИОС" to {
                    settingsPreferences.edit().putString("initial_route", "account").apply()
                    reloadSettingsScreen()
                }
            else
                "Настройки" to {
                    settingsPreferences.edit().putString("initial_route", "settings").apply()
                    reloadSettingsScreen()
                }
        )
    )
}

fun selectUser(result: MutableState<Boolean> = mutableStateOf(false)){
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
                result.value = true
                navBarUpdate()
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
                result.value = true
                navBarUpdate()
            }
        )
    )
}