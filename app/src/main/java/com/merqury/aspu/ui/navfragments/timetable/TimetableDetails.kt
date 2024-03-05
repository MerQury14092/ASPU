package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.enums.TimetableDisciplineType
import com.merqury.aspu.services.openMapWithMarker
import com.merqury.aspu.ui.navfragments.timetable.DTO.Discipline
import com.merqury.aspu.ui.navfragments.timetable.DTO.getCorpsByAudience
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

fun showDisciplineDetails(discipline: Discipline) {
    showSimpleModalWindow(
        containerColor = theme.value[SurfaceTheme.background]!!
    ) {
        DisciplineDetails(discipline = discipline)
    }
}

@Composable
private fun DisciplineDetails(discipline: Discipline) {
    @Composable
    fun AlignText(text: String, textAlign: TextAlign = TextAlign.Left) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = textAlign,
            color = theme.value[SurfaceTheme.text]!!
        )
    }

    @Composable
    fun ThemeDivider() {
        Divider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = theme.value[SurfaceTheme.divider]!!
        )
    }

    Box(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(.7f)
    ) {
        Column {
            AlignText(discipline.time, TextAlign.Center)
            AlignText(discipline.name, TextAlign.Center)
            AlignText(
                TimetableDisciplineType.valueOf(discipline.type).localizedName,
                TextAlign.Center
            )
            ThemeDivider()

            val corps = getCorpsByAudience(discipline.audienceID)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                Column {
                    Text(
                        if (discipline.audienceID == "Спортзал ФОК")
                            "Спортзал"
                        else
                            "Аудитория: ${discipline.audienceID}",
                        color = theme.value[SurfaceTheme.text]!!
                    )
                    Text(
                        corps.name,
                        color = theme.value[SurfaceTheme.text]!!
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.map),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            openMapWithMarker(corps.lat, corps.lon, corps.name)
                        },
                    colorFilter = ColorFilter.tint(theme.value[SurfaceTheme.text]!!)
                )
            }

            ThemeDivider()
            AlignText(discipline.groupName)
            ThemeDivider()
            AlignText(discipline.teacherName)
            ThemeDivider()
            AlignText(
                "Подгруппа: ${
                    if (discipline.subgroup != 0)
                        discipline.subgroup.toString()
                    else
                        "общая"
                }"
            )
            if (discipline.distant) {
                ThemeDivider()
                AlignText("Дистанционно", TextAlign.Center)
            }
        }
    }
}