package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow

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
        Text(text = "Настроить политику показа дисциплин по выбору")
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