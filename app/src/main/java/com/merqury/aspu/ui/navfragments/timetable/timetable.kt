package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.merqury.aspu.services.getTimetableByDate
import com.merqury.aspu.services.getTodayDate
import com.merqury.aspu.ui.SwipeableBox
import com.merqury.aspu.ui.navfragments.settings.selectableDisciplines
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.showSimpleModalWindow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val selectedId = mutableStateOf(settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")!!)
val selectedOwner = mutableStateOf(settingsPreferences.getString("timetable_id_owner", "GROUP")!!)
val selectedDate = mutableStateOf(getTodayDate())
val timetableLoaded = mutableStateOf(false)
val timetableDay = mutableStateOf(JSONObject())
val timetableLoadSuccess = mutableStateOf(true)

@Composable
fun TimetableScreen(header: MutableState<@Composable () -> Unit>) {
    TimetableScreenContent(header)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetableScreenContent(header: MutableState<@Composable () -> Unit>) {
    Column {
        header.value = { TimetableHeader() }
        val pullRefreshState = rememberPullRefreshState(
            refreshing = !timetableLoaded.value,
            onRefresh = {
                timetableLoaded.value = false
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (!timetableLoaded.value) {
                getTimetableByDate(
                    selectedId.value,
                    selectedOwner.value,
                    selectedDate.value,
                    timetableDay,
                    timetableLoaded,
                    timetableLoadSuccess
                )
            } else {
                if (timetableLoadSuccess.value)
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
                            val disciplines = if (
                                settingsPreferences.getBoolean("filtration_on", false)
                                && settingsPreferences.getString("user", "student") == "student"
                                && selectedId.value == settingsPreferences.getString(
                                    "timetable_id",
                                    "ВМ-ИВТ-2-1"
                                )
                            )
                                filter(timetableDay.value)
                            else
                                timetableDay.value.getJSONArray("disciplines")

                            LazyColumn {
                                items(count = disciplines.length()) {
                                    TimetableItem(
                                        discipline = disciplines.get(it) as JSONObject
                                    )
                                }

                            }
                        }
                    }
                else
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Ошибка загрузки расписания!")
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
    (0..<timetableDay.getJSONArray("disciplines").length()).forEach {
        val currentDiscipline = timetableDay.getJSONArray("disciplines").get(it) as JSONObject
        if (isSelectableDiscipline(currentDiscipline.getString("name")))
            filterSelectableDiscipline(disciplines, currentDiscipline)
        else
            filterBySubgroup(disciplines, currentDiscipline)
    }
    return disciplines
}

fun filterBySubgroup(res: JSONArray, discipline: JSONObject) {
    if (
        discipline.getInt("subgroup") == 0
        ||
        discipline.getInt("subgroup") == settingsPreferences.getInt("selected_subgroup", 0)
        ||
        settingsPreferences.getInt("selected_subgroup", 0) == 0
    )
        res.put(discipline)
}

fun filterSelectableDiscipline(res: JSONArray, discipline: JSONObject) {
    val factName = getNameOfSelectableDiscipline(discipline.getString("name"))
    if (!selectableDisciplines.contains(factName)) {
        answerShowingSelectableDiscipline(factName)
        selectableDisciplines.edit().putBoolean(factName, false).apply()
    } else {
        if (selectableDisciplines.getBoolean(factName, false)) {
            discipline.put("name", factName)
            res.put(discipline)
        }
    }
}

fun answerShowingSelectableDiscipline(name: String) {
    showSimpleModalWindow(
        closeable = false
    ) {
        Column {
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Показывать дисциплину по выбору: $name?")
                }
            }
            Row {

                Card(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .clickable {
                            selectableDisciplines
                                .edit()
                                .putBoolean(name, true)
                                .apply()
                            it.value = false
                            timetableLoaded.value = false
                        }
                        .padding(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Да")
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectableDisciplines
                                .edit()
                                .putBoolean(name, false)
                                .apply()
                            it.value = false
                            timetableLoaded.value = false
                        }
                        .padding(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Нет")
                    }
                }
            }
        }
    }
}


fun isSelectableDiscipline(name: String): Boolean {
    return name.contains("Дисциплина по выбору \"")
}

fun getNameOfSelectableDiscipline(name: String): String {
    return name.substring(22..<name.length - 1)
}