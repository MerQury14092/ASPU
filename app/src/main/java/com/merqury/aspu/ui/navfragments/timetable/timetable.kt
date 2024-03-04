package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.background
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
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merqury.aspu.services.getTimetableByDate
import com.merqury.aspu.services.getTodayDate
import com.merqury.aspu.ui.SwipeableBox
import com.merqury.aspu.ui.navfragments.settings.selectableDisciplines
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.DTO.Discipline
import com.merqury.aspu.ui.navfragments.timetable.DTO.TimetableDay
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val selectedId = mutableStateOf(settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1")!!)
val selectedOwner = mutableStateOf(settingsPreferences.getString("timetable_id_owner", "GROUP")!!)
val selectedDate = mutableStateOf(getTodayDate())
val timetableLoaded = mutableStateOf(false)
val timetableDay = mutableStateOf(TimetableDay("","", "", listOf()))
val timetableLoadSuccess = mutableStateOf(true)


@Composable
fun TimetableScreen(header: MutableState<@Composable () -> Unit>) {
    TimetableScreenContent(header)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetableScreenContent(header: MutableState<@Composable () -> Unit>) {
    Column {
        header.value = {
            TimetableHeader()
        }
        val pullRefreshState = rememberPullRefreshState(
            refreshing = !timetableLoaded.value,
            onRefresh = {
                timetableLoaded.value = false
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.value[SurfaceTheme.background]!!)
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
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = theme.value[SurfaceTheme.background]!!)
                    ) {
                        if (timetableDay.value.disciplines.size == 0)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Нет пар", color = theme.value[SurfaceTheme.text]!!)
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
                                timetableDay.value.disciplines

                            LazyColumn {
                                items(count = disciplines.size) {
                                    TimetableItem(
                                        discipline = disciplines[it]
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
                            .verticalScroll(rememberScrollState())
                            .background(color = theme.value[SurfaceTheme.background]!!),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ошибка загрузки расписания!",
                            color = theme.value[SurfaceTheme.text]!!
                        )
                    }
            }
            PullRefreshIndicator(
                refreshing = !timetableLoaded.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = theme.value[SurfaceTheme.foreground]!!,
                contentColor = theme.value[SurfaceTheme.text]!!
            )
        }
    }
}

fun filter(
    timetableDay: TimetableDay
): ArrayList<Discipline> {
    var disciplines = arrayListOf<Discipline>()
    if (timetableDay.id == settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1"))
        (0..<timetableDay.disciplines.size).forEach {
            val currentDiscipline = timetableDay.disciplines[it]
            if (isSelectableDiscipline(currentDiscipline.name))
                filterSelectableDiscipline(disciplines, currentDiscipline)
            else
                filterBySubgroup(disciplines, currentDiscipline)
        }
    else
        disciplines = ArrayList(timetableDay.disciplines)
    return disciplines
}

fun filterBySubgroup(res: ArrayList<Discipline>, discipline: Discipline) {
    if (
        discipline.subgroup == 0
        ||
        discipline.subgroup == settingsPreferences.getInt("selected_subgroup", 0)
        ||
        settingsPreferences.getInt("selected_subgroup", 0) == 0
    )
        res.add(discipline)
}

fun filterSelectableDiscipline(res: ArrayList<Discipline>, discipline: Discipline) {
    val factName = getNameOfSelectableDiscipline(discipline.name)
    if (!selectableDisciplines.contains(factName)) {
        answerShowingSelectableDiscipline(factName)
        selectableDisciplines.edit().putBoolean(factName, false).apply()
    } else {
        if (selectableDisciplines.getBoolean(factName, false)) {
            discipline.name = factName
            res.add(discipline)
        }
    }
}

fun answerShowingSelectableDiscipline(name: String) {
    showSimpleModalWindow(
        closeable = false,
        containerColor = theme.value[SurfaceTheme.background]!!
    ) {
        Column {
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Показывать дисциплину по выбору: $name?",
                        color = theme.value[SurfaceTheme.text]!!
                    )
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
                        .padding(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = theme.value[SurfaceTheme.foreground]!!
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Да", color = theme.value[SurfaceTheme.text]!!)
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
                        .padding(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = theme.value[SurfaceTheme.foreground]!!
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Нет", color = theme.value[SurfaceTheme.text]!!)
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