package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.enums.TimetableDisciplineType
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.DTO.Discipline
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

@Composable
fun TimetableItem(discipline: Discipline) {
    val type = TimetableDisciplineType.valueOf(discipline.type)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
//            .shadow(
//                elevation = 5.dp,
//                shape = RoundedCornerShape(15.dp),
//                spotColor = Color.Black
//            )
        ,
        colors = CardDefaults.cardColors(
            containerColor =
            if (settingsPreferences.getBoolean("color_timetable", true))
                Color(type.colorInt)
            else
                theme.value[SurfaceTheme.foreground]!!

        )
    ) {
        Box(modifier = Modifier.padding(5.dp)) {
            Column {
                Text(
                    discipline.time,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = if (settingsPreferences.getBoolean(
                            "color_timetable",
                            true
                        )
                    ) Color.Black else theme.value[SurfaceTheme.text]!!
                )
                Text(
                    discipline.name,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = if (settingsPreferences.getBoolean(
                            "color_timetable",
                            true
                        )
                    ) Color.Black else theme.value[SurfaceTheme.text]!!
                )
                Row (
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    if (selectedOwner.value.lowercase() != "classroom")
                        Row {
                            Text(
                                "Аудитория: ",
                                color = if (settingsPreferences.getBoolean(
                                        "color_timetable",
                                        true
                                    )
                                ) Color.Black else theme.value[SurfaceTheme.text]!!,
                                modifier = Modifier.padding(start = 10.dp)
                            )

                            Text(
                                text = discipline.audienceID,
                                fontWeight = FontWeight.Bold,
                                color = if (settingsPreferences.getBoolean(
                                        "color_timetable",
                                        true
                                    )
                                ) Color.Black else theme.value[SurfaceTheme.enable]!!
                            )
                        }
                    else
                        if (selectedOwner.value.lowercase() != "group")
                            Text(
                                multiplyGroupFilter(discipline.groupName),
                                color = if (settingsPreferences.getBoolean(
                                        "color_timetable",
                                        true
                                    )
                                ) Color.Black else theme.value[SurfaceTheme.text]!!,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                    if (selectedOwner.value.lowercase() != "teacher")
                        Text(
                            discipline.teacherName,
                            color = if (settingsPreferences.getBoolean(
                                    "color_timetable",
                                    true
                                )
                            ) Color.Black else theme.value[SurfaceTheme.text]!!,
                            modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                            textAlign = TextAlign.End,
                        )
                    if (selectedOwner.value.lowercase() == "teacher")
                        Text(
                            multiplyGroupFilter(discipline.groupName),
                            color = if (settingsPreferences.getBoolean(
                                    "color_timetable",
                                    true
                                )
                            ) Color.Black else theme.value[SurfaceTheme.text]!!,
                            modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                            textAlign = TextAlign.End,

                        )
                }
                if (!settingsPreferences.getBoolean("filtration_on", true) ||
                    (settingsPreferences.getInt("selected_subgroup", 0) == 0
                            || settingsPreferences.getString(
                        "timetable_id",
                        "ВМ-ИВТ-2-1"
                    ) != selectedId.value)
                ) {
                    if (discipline.subgroup != 0)
                        Text(
                            "Подгруппа: ${discipline.subgroup}",
                            color = if (settingsPreferences.getBoolean(
                                    "color_timetable",
                                    true
                                )
                            ) Color.Black else theme.value[SurfaceTheme.text]!!,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                }
                Box {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            getDisciplineNumberByTime(discipline.time).toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settingsPreferences.getBoolean(
                                    "color_timetable",
                                    true
                                )
                            ) Color.Black else theme.value[SurfaceTheme.text]!!

                        )
                    }
                    Column {

                        if (type != TimetableDisciplineType.none)
                            Text(
                                type.localizedName,
                                color = if (settingsPreferences.getBoolean(
                                        "color_timetable",
                                        true
                                    )
                                ) Color.Black else theme.value[SurfaceTheme.text]!!,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontStyle =  FontStyle.Italic
                            )
                    }
                }
                if (discipline.distant) {
                    Divider(color = theme.value[SurfaceTheme.divider]!!)
                    Text(
                        text = "ДИСТАНЦИОННО",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = if (settingsPreferences.getBoolean(
                                "color_timetable",
                                true
                            )
                        ) Color.Black else theme.value[SurfaceTheme.text]!!
                    )
                }
            }
        }
    }
}

private fun multiplyGroupFilter(group: String): String {
    if(group.split(",").size > 1)
        return "Несколько групп"
    return group
}

private fun getDisciplineNumberByTime(time: String): Int {
    return when (time) {
        "8:00-9:30" -> 1
        "9:40-11:10" -> 2
        "11:40-13:10" -> 3
        "13:30-15:00" -> 4
        "15:10-16:40" -> 5
        "16:50-18:20" -> 6
        "18:30-20:00" -> 7
        else -> 8
    }
}