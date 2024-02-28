package com.merqury.aspu.ui.navfragments.timetable

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.merqury.aspu.context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun TimetableHeaderPreview() {
    Box(Modifier.fillMaxSize()) {
        TimetableHeader()
    }
}
@Composable
fun TimetableHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .background(Color.Gray)
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
    if(date == today)
        return "Сегодня"
    else if(date == today.plusDays(1))
        return "Завтра"
    else if(date == today.minusDays(1))
        return "Вчера"
    return rawDate
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
