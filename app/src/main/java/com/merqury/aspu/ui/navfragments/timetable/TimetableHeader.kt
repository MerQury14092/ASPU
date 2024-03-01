package com.merqury.aspu.ui.navfragments.timetable

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.merqury.aspu.context
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val date = LocalDate.parse(selectedDate.value, formatter)
                DatePickerDialog(
                    context!!,
                    { _, year, month, day ->
                        changeDate(day, month, year)
                        timetableLoaded.value = false
                    },
                    date.year,
                    date.monthValue-1,
                    date.dayOfMonth
                ).show()
            }) {
                Text("${prettyDate(selectedDate.value)}, ${dayOfWeek(selectedDate.value)}")
            }
            Button(onClick = {
                showSelectIdModalWindow {
                    selectedId.value = it.name
                    selectedOwner.value = it.owner.uppercase()
                    timetableLoaded.value = false
                }
            }) {
                Text(text = selectedId.value)
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
        today.minusDays(1) -> "Вчера"
        else -> rawDate
    }
}

fun dayOfWeek(rawDate: String): String{
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.parse(rawDate, formatter)
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, context!!.resources.configuration.locale)
}

fun changeDate(
    day: Int,
    month: Int,
    year: Int
){
    var newDate = ""
    newDate += if(day < 10)
        "0$day."
    else
        "$day."
    newDate += if(month+1 < 10)
        "0${month+1}."
    else
        "${month+1}."
    newDate += year.toString()
    selectedDate.value = newDate
}
