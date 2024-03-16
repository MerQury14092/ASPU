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