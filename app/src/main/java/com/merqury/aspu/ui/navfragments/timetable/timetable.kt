package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merqury.aspu.mainCoroutineScope
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.timetable.getTimetableByDate
import com.merqury.aspu.services.timetable.getTodayDate
import com.merqury.aspu.services.timetable.models.Discipline
import com.merqury.aspu.services.timetable.models.TimetableDay
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.settings.selectableDisciplines
import com.merqury.aspu.ui.selected_page
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val selectedId = mutableStateOf(AppSettings.timetableId)
val selectedOwner = mutableStateOf(AppSettings.timetableIdOwner)
private val pointDate = getTodayDate()
private var loaded by mutableStateOf(false)

@OptIn(ExperimentalFoundationApi::class)
private val pagerState = PagerState(
    currentPage = Int.MAX_VALUE / 2,
    pageCount = { Int.MAX_VALUE }
)

@Composable
fun TimetableScreen(header: MutableState<@Composable () -> Unit>) {
    val useConfig = AppConfig.useTimetablePageConfig()
    if (useConfig.canUse)
        TimetableContent(header)
    else
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceTheme.background.color),
            contentAlignment = Alignment.Center
        ) {
            header.value = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    TitleHeader(title = "Расписание")
                }
            }
            ThemeText(
                text = useConfig.reason ?: "Раснисание пока не работает в данной версии",
                textAlign = TextAlign.Center
            )
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetableContent(header: MutableState<@Composable () -> Unit>) {
    Column {
        if (selectedTimetableRoute)
            header.value = {
                TimetableHeader(getDateByPage(pagerState.currentPage))
            }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceTheme.background.color),
            verticalAlignment = Alignment.Top,
            outOfBoundsPageCount = 1
        ) {
            TimetableDay(page = it)
        }
    }
}

private inline val selectedTimetableRoute get() = selected_page.value == "timetable"

@Composable
private fun TimetableDay(
    page: Int
) {
    var disciplines by remember {
        mutableStateOf<List<Discipline>?>(null)
    }
    var timetableLoaded by remember {
        mutableStateOf(false)
    }
    var errorString by remember {
        mutableStateOf<String?>(null)
    }
    if (!timetableLoaded || !loaded) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SurfaceTheme.background.color)
        ) {
            TimetableItemLoadingPlaceholder()
            TimetableItemLoadingPlaceholder()
            TimetableItemLoadingPlaceholder()
        }
        getTimetableByDate(
            getDateByPage(page),
            {
                errorString = it
                timetableLoaded = true
                loaded = true
            }
        ) {
            async {
                disciplines = if (
                    AppSettings.timetableFiltration
                    && AppSettings.whoIsUser == "student"
                    && selectedId.value == AppSettings.timetableId
                )
                    filter(it)
                else
                    it.disciplines
                timetableLoaded = true
                loaded = true
            }
        }
    } else {
        if (errorString != null)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceTheme.background.color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorString!!, color = SurfaceTheme.text.color)
            }
        else if (disciplines!!.isEmpty())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceTheme.background.color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Нет пар", color = SurfaceTheme.text.color)
            }
        else {
            LazyColumn {
                items(count = disciplines!!.size) {
                    TimetableItem(
                        discipline = disciplines!![it]
                    )
                }

            }
        }
    }
}

private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private fun getDateByPage(page: Int): String {
    if (Int.MAX_VALUE / 2 == page) {
        return pointDate
    }
    val point = LocalDate.parse(pointDate, formatter)
    return point.plusDays((page - Int.MAX_VALUE / 2).toLong()).format(formatter)
}

@OptIn(ExperimentalFoundationApi::class)
fun setTimetableDate(date: String) {
    val localDate = LocalDate.parse(date, formatter)
    mainCoroutineScope.launch {
        pagerState.scrollToPage(
            Int.MAX_VALUE / 2 + datesBetween(
                localDate,
                LocalDate.parse(pointDate, formatter)
            )
        )
    }
}

private fun datesBetween(
    firstDate: LocalDate,
    secondDate: LocalDate
): Int {
    return (firstDate.toEpochDay() - secondDate.toEpochDay()).toInt()
}

fun filter(
    timetableDay: TimetableDay
): ArrayList<Discipline> {
    var disciplines = arrayListOf<Discipline>()
    if (timetableDay.id == AppSettings.timetableId)
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
        discipline.subgroup == AppSettings.selectedSubgroup
        ||
        AppSettings.selectedSubgroup == 0
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
        containerColor = SurfaceTheme.background.colorWithoutAnim
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
                        color = SurfaceTheme.text.color
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
                            loaded = false
                        }
                        .padding(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceTheme.foreground.color
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Да", color = SurfaceTheme.text.color)
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
                            loaded = false
                        }
                        .padding(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceTheme.foreground.color
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Нет", color = SurfaceTheme.text.color)
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