package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.merqury.aspu.services.getTimetableByDate
import org.json.JSONObject

val selectedId = mutableStateOf("ВМ-ИВТ-2-1")
val selectedOwner = mutableStateOf("GROUP")
val selectedDate = mutableStateOf("27.02.2024")
val timetableLoaded = mutableStateOf(false)
val timetableDay = mutableStateOf(JSONObject())
val refreshing = mutableStateOf(false)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetableScreen() {
    Column {
        TimetableHeader()
        val pullRefreshState = rememberPullRefreshState(
            refreshing = !timetableLoaded.value,
            onRefresh = {
                timetableLoaded.value = false
            })
        Box (modifier = Modifier.pullRefresh(pullRefreshState).fillMaxWidth()) {
            if (!timetableLoaded.value) {
                getTimetableByDate(
                    selectedId.value,
                    selectedOwner.value,
                    selectedDate.value,
                    timetableDay,
                    timetableLoaded
                )
            }
            else {
                LazyColumn{
                    items(count = timetableDay.value.getJSONArray("disciplines").length()){
                        TimetableItem(
                            discipline = timetableDay.value.getJSONArray("disciplines").get(it) as JSONObject
                        )
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = !timetableLoaded.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Color.White
            )
        }
    }
}