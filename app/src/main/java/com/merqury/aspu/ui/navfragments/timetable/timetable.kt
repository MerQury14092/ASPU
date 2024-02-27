package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.getTimetableByDate
import org.json.JSONObject

val selectedId = mutableStateOf("ВМ-ИВТ-2-1")
val selectedOwner = mutableStateOf("GROUP")
val selectedDate = mutableStateOf("27.02.2024")
val timetableLoaded = mutableStateOf(false)
val timetableDay = mutableStateOf(JSONObject())
val timetableImageData = mutableStateOf(byteArrayOf())
val timetableImageLoaded = mutableStateOf(false)

@Composable
fun TimetableScreen() {

    Column {
        TimetableHeader()
        if (!timetableLoaded.value) {
            getTimetableByDate(
                selectedId.value,
                selectedOwner.value,
                selectedDate.value,
                timetableDay,
                timetableLoaded
            )
            Text(text = "Loading", fontSize = 100.sp)
        } else {
            Text(text = timetableDay.value.toString())
        }
    }
}