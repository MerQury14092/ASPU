package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.merqury.aspu.services.getTimetableByDate
import com.merqury.aspu.services.getTodayDate
import com.merqury.aspu.ui.SwipeableBox
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val selectedId = mutableStateOf(settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")!!)
val selectedOwner = mutableStateOf(settingsPreferences.getString("timetable_id_owner", "GROUP")!!)
val selectedDate = mutableStateOf(getTodayDate())
val timetableLoaded = mutableStateOf(false)
val timetableDay = mutableStateOf(JSONObject())

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetableScreen() {
    TimetableScreenContent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetableScreenContent() {
    Column {
        TimetableHeader()
        val pullRefreshState = rememberPullRefreshState(
            refreshing = !timetableLoaded.value,
            onRefresh = {
                timetableLoaded.value = false
            })
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
        ) {
            if (!timetableLoaded.value) {
                getTimetableByDate(
                    selectedId.value,
                    selectedOwner.value,
                    selectedDate.value,
                    timetableDay,
                    timetableLoaded
                )
            } else {
                SwipeableBox(
                    onSwipeLeft = {
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        var currentDate = LocalDate.parse(selectedDate.value, formatter)
                        currentDate = currentDate.plusDays(-1)
                        selectedDate.value = currentDate.format(formatter)
                        timetableLoaded.value = false
                    },
                    onSwipeRight = {
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        var currentDate = LocalDate.parse(selectedDate.value, formatter)
                        currentDate = currentDate.plusDays(1)
                        selectedDate.value = currentDate.format(formatter)
                        timetableLoaded.value = false
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (timetableDay.value.getJSONArray("disciplines").length() == 0)
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Пусто")
                        }
                    else {
                        val disciplines = filter(timetableDay.value)

                        LazyColumn {
                            items(count = disciplines.length()) {
                                TimetableItem(
                                    discipline = disciplines.get(it) as JSONObject
                                )
                            }

                        }
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

fun filter(
    timetableDay: JSONObject
): JSONArray {
    val disciplines = JSONArray()
    val filterSubgroup = settingsPreferences.getInt("selected_subgroup", 0)
    (0..<timetableDay.getJSONArray("disciplines").length()).forEach {
        val currentDiscipline = timetableDay.getJSONArray("disciplines").get(it) as JSONObject
        if(filterSubgroup != 0 && currentDiscipline.getInt("subgroup") == filterSubgroup)
            disciplines.put(currentDiscipline)
        else
            disciplines.put(currentDiscipline)
    }
    return disciplines
}