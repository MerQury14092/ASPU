package com.merqury.aspu.ui.navfragments.timetable

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.merqury.aspu.appContext
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun TimetableHeader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val date = LocalDate.parse(selectedDate.value, formatter)
                    DatePickerDialog(
                        appContext!!,
                        { _, year, month, day ->
                            changeDate(day, month, year)
                            timetableLoaded.value = false
                        },
                        date.year,
                        date.monthValue - 1,
                        date.dayOfMonth
                    ).show()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = theme.value[SurfaceTheme.button]!!
                )
            ) {
                Text(
                    "${prettyDate(selectedDate.value)}, ${dayOfWeek(selectedDate.value)}",
                    color = theme.value[SurfaceTheme.text]!!,
                    fontSize = 11.sp
                )
            }
            Button(
                onClick = {
                    showSelectIdModalWindow {
                        selectedId.value = it.searchContent
                        selectedOwner.value = it.type.uppercase()
                        timetableLoaded.value = false
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = theme.value[SurfaceTheme.button]!!
                )
            ) {
                Text(
                    text = selectedId.value,
                    color = theme.value[SurfaceTheme.text]!!,
                    fontSize = 11.sp
                )
            }
        }
    }
}

fun prettyDate(
    rawDate: String
): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.parse(rawDate, formatter)
    val today = LocalDate.now()
    return when (date) {
        today -> "Сегодня"
        today.plusDays(1) -> "Завтра"
        today.plusDays(2) -> "Послезавтра"
        today.minusDays(1) -> "Вчера"
        today.minusDays(2) -> "Позавчера"
        else -> humanDate(rawDate)
    }
}

fun humanDate(
    rawDate: String
): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.parse(rawDate, formatter)
    val today = LocalDate.now()
    val year = if (today.year == date.year) "" else date.year.toString()
    return "${date.dayOfMonth} ${
        date.month.getDisplayName(
            TextStyle.FULL,
            appContext!!.resources.configuration.locale
        )
    } $year"
}

fun dayOfWeek(rawDate: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.parse(rawDate, formatter)
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, appContext!!.resources.configuration.locale)
}

fun changeDate(
    day: Int,
    month: Int,
    year: Int
) {
    var newDate = ""
    newDate += if (day < 10)
        "0$day."
    else
        "$day."
    newDate += if (month + 1 < 10)
        "0${month + 1}."
    else
        "${month + 1}."
    newDate += year.toString()
    selectedDate.value = newDate
}
