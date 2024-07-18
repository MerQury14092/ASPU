package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.showSelectListDialog

fun selectInitialSubgroup() {
    showSelectListDialog(
        mapOf(
            "Нет" to {
                AppSettings.selectedSubgroup = 0
            },
            "1" to {
                AppSettings.selectedSubgroup = 1

            },
            "2" to {
                AppSettings.selectedSubgroup = 2
            }
        )
    )
}

fun selectInitialRoute() {
    showSelectListDialog(
        mapOf(
            "Новости" to {
                AppSettings.initialRoute = "news"
            },
            "Расписание" to {
                AppSettings.initialRoute = "timetable"
            },
            "Студенту" to {
                AppSettings.initialRoute = "other"
            },
            if (AppSettings.eiosLogged)
                "Аккаунт ЭИОС" to {
                    AppSettings.initialRoute = "account"
                }
            else
                "Настройки" to {
                    AppSettings.initialRoute = "settings"
                }
        )
    )
}

fun selectUser(result: MutableState<Boolean> = mutableStateOf(false)) {
    showSelectListDialog(
        mapOf(
            "Студент" to {
                AppSettings.whoIsUser = "student"
                AppSettings.timetableId = "ВМ-ИВТ-3-1"
                AppSettings.timetableIdOwner = "GROUP"
                selectableDisciplines.edit().clear().apply()
                result.value = true
            },
            "Преподаватель" to {
                AppSettings.whoIsUser = "teacher"
                AppSettings.timetableId = "Лапшин Н.А.,ст.пр. "
                AppSettings.timetableIdOwner = "TEACHER"
                selectableDisciplines.edit().clear().apply()
            }
        )
    )
}