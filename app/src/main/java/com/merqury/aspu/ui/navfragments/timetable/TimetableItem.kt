package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
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
            .clickable {
                showDisciplineDetails(discipline)
            },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = if (settingsPreferences.getBoolean(
                            "color_timetable",
                            true
                        )
                    ) Color.Black else theme.value[SurfaceTheme.text]!!
                )
                Row(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    if (selectedOwner.value.lowercase() != "classroom") {
                        if (!discipline.distant) {
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
                        } else {
                            Text(
                                text = "Дистанционно",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 10.dp),
                                color = if (settingsPreferences.getBoolean(
                                        "color_timetable",
                                        true
                                    )
                                ) Color.Black else theme.value[SurfaceTheme.enable]!!
                            )
                        }
                    } else
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp),
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
                                fontStyle = FontStyle.Italic
                            )
                    }
                }
                if (discipline.distant && selectedOwner.value.lowercase() == "classroom") {
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

@Composable
fun TimetableItemLoadingPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = theme.value[SurfaceTheme.foreground]!!

        )
    ) {
        Box(modifier = Modifier.padding(5.dp)) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        "18:00-19:30",
                        modifier = Modifier
                            .placeholder(
                                visible = true,
                                color = theme.value[SurfaceTheme.placeholder_primary]!!,
                                highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                                shape = RoundedCornerShape(15.dp)
                            ),
                        textAlign = TextAlign.Center,
                        color = if (settingsPreferences.getBoolean(
                                "color_timetable",
                                true
                            )
                        ) Color.Black else theme.value[SurfaceTheme.text]!!
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        "Разработка web-приложений",
                        modifier = Modifier
                            .padding(bottom = 10.dp, top = 5.dp)
                            .placeholder(
                                visible = true,
                                color = theme.value[SurfaceTheme.placeholder_primary]!!,
                                highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                                shape = RoundedCornerShape(15.dp)
                            ),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = if (settingsPreferences.getBoolean(
                                "color_timetable",
                                true
                            )
                        ) Color.Black else theme.value[SurfaceTheme.text]!!
                    )
                }
                Row(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Row {
                        Text(
                            "Аудитория: 3",
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .placeholder(
                                    visible = true,
                                    color = theme.value[SurfaceTheme.placeholder_primary]!!,
                                    highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                                    shape = RoundedCornerShape(15.dp)
                                )
                        )
                    }
                }
                if (!settingsPreferences.getBoolean("filtration_on", true) ||
                    (settingsPreferences.getInt("selected_subgroup", 0) == 0
                            || settingsPreferences.getString(
                        "timetable_id",
                        "ВМ-ИВТ-2-1"
                    ) != selectedId.value)
                ) {
                    Text(
                        "Place 4",
                        color = if (settingsPreferences.getBoolean(
                                "color_timetable",
                                true
                            )
                        ) Color.Black else theme.value[SurfaceTheme.text]!!,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .placeholder(
                                visible = true,
                                color = theme.value[SurfaceTheme.placeholder_primary]!!,
                                highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                                shape = RoundedCornerShape(15.dp)
                            )
                    )
                }
                Box {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            "1",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settingsPreferences.getBoolean(
                                    "color_timetable",
                                    true
                                )
                            ) Color.Black else theme.value[SurfaceTheme.text]!!,
                            modifier = Modifier.placeholder(
                                visible = true,
                                color = theme.value[SurfaceTheme.placeholder_primary]!!,
                                highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                                shape = RoundedCornerShape(15.dp)
                            )

                        )
                    }
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                "Лаб. работа",
                                color = if (settingsPreferences.getBoolean(
                                        "color_timetable",
                                        true
                                    )
                                ) Color.Black else theme.value[SurfaceTheme.text]!!,
                                modifier = Modifier.placeholder(
                                    visible = true,
                                    color = theme.value[SurfaceTheme.placeholder_primary]!!,
                                    highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                                    shape = RoundedCornerShape(15.dp)
                                ),
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun multiplyGroupFilter(group: String): String {
    if (group.split(",").size > 1)
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