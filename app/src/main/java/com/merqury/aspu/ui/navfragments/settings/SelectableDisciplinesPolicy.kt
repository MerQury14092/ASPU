package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merqury.aspu.ui.navfragments.timetable.timetableLoaded
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim


fun showSelectableDisciplinesPreferences() {
    showSimpleUpdatableModalWindow(
        containerColor = SurfaceTheme.background.colorWithoutAnim
    ) { _, update, forUpdate ->
        Box(modifier = Modifier.padding(10.dp)){
            Column {
                if (selectableDisciplines.all.isEmpty())
                    Text(text = "Пусто", color = SurfaceTheme.text.color)
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
                            Text(text = "${it.key}: ${if(selectableDisciplines.getBoolean(it.key, true)) "Показывать" else "Не показывать"}", color = SurfaceTheme.text.color)
                        }
                    }
            }
        }
    }
}