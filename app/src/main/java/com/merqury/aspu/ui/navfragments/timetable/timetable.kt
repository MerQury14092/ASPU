package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.services.getTimetableByDate
import com.merqury.aspu.ui.GifImage
import org.json.JSONObject

val selectedId = mutableStateOf("ВМ-ИВТ-2-1")
val selectedOwner = mutableStateOf("GROUP")
val selectedDate = mutableStateOf("27.02.2024")
val timetableLoaded = mutableStateOf(false)
val timetableDay = mutableStateOf(JSONObject())

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GifImage(
                    modifier = Modifier.size(50.dp),
                    gifResourceId = R.drawable.loading,
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            LazyColumn{
                items(count = timetableDay.value.getJSONArray("disciplines").length()){
                    TimetableItem(
                        discipline = timetableDay.value.getJSONArray("disciplines").get(it) as JSONObject
                    )
                }
            }
        }
    }
}