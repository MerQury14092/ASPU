package com.merqury.aspu.ui.navfragments.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merqury.aspu.appContext
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

fun filterSettings() {
    showSimpleUpdatableModalWindow (
        containerColor = theme.value[SurfaceTheme.background]!!
    ){ _, update, forUpdate ->
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
                "Включить фильтрацию пар",
            color = theme.value[SurfaceTheme.text]!!
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
            }", color = theme.value[SurfaceTheme.text]!!
        )
    }
}

@Composable
fun ShowSelectableDisciplinesPreferencesButton() {
    SettingsButton(onClick = {
        showSelectableDisciplinesPreferences()
    }) {
        Text(text = "Настроить политику показа дисциплин по выбору", color = theme.value[SurfaceTheme.text]!!)
    }
}

@Composable
fun ClearSelectableDisciplinesPolicy(){
    SettingsButton(onClick = {
        selectableDisciplines.edit().clear().apply()
        timetableLoaded.value = false
        Toast.makeText(appContext, "Очищено", Toast.LENGTH_LONG).show()
    }) {
        Text(text = "Очистить политику показа дисциплин по выбору", color = theme.value[SurfaceTheme.text]!!)
    }
}

fun showSelectableDisciplinesPreferences() {
    showSimpleUpdatableModalWindow(
        containerColor = theme.value[SurfaceTheme.background]!!
    ) { _, update, forUpdate ->
        Box(modifier = Modifier.padding(10.dp)){
            Column {
                if (selectableDisciplines.all.isEmpty())
                    Text(text = "Пусто", color = theme.value[SurfaceTheme.text]!!)
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
                            Text(text = "${it.key}: ${if(selectableDisciplines.getBoolean(it.key, true)) "Показывать" else "Не показывать"}", color = theme.value[SurfaceTheme.text]!!)
                        }
                    }
            }
        }
    }
}