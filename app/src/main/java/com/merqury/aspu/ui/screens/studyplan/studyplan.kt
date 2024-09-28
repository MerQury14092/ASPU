package com.merqury.aspu.ui.screens.studyplan

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.studyplan.PlanElement
import com.merqury.aspu.services.studyplan.getStudyPlan
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.UiState
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.placeholder
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun StudyPlanScreen(
    planId: Int,
    header: MutableState<@Composable () -> Unit>,
    startSemester: Int = 1
) {
    var uiState by remember {
        mutableStateOf(UiState.IDLE)
    }
    var studyPlan by remember {
        mutableStateOf<List<PlanElement>?>(null)
    }
    var errorString by remember {
        mutableStateOf<String?>(null)
    }
    var pagerState by remember {
        mutableStateOf(PagerState(currentPage = startSemester - 1) { startSemester - 1 })
    }
    header.value = {
        TitleHeader(title = "${(pagerState.currentPage) / 2 + 1} курс, ${pagerState.currentPage + 1} семестр")
    }
    when (uiState) {
        UiState.IDLE -> {
            LaunchedEffect(uiState) {
                uiState = UiState.LOADING
                getStudyPlan(
                    planId,
                    {
                        errorString = "Ошибка загрузки данных"
                        uiState = UiState.LOADED
                    }
                ) {
                    var plan = it.map { planElement ->
                        planElement.blockName = planElement.blockName.replace(
                            Regex("(.*[ОВ]\\.).*(ДВ.*)"),
                            "$1$2"
                        )
                        planElement
                    }
                    if (!AppSettings.Eios.showBlockNumber) {
                        plan = plan.map { planElement ->
                            planElement.blockName = planElement.blockName.replace(
                                Regex("(Б.\\.[ОВ])(\\.\\d+)"),
                                "$1"
                            )
                            planElement
                        }
                    }
                    studyPlan = plan
                    uiState = UiState.LOADED
                    pagerState =
                        PagerState(currentPage = startSemester - 1) {
                            studyPlan!!.maxOfOrNull { maxSemester -> maxSemester.semester }!!
                        }
                }
            }
        }

        UiState.LOADING -> {
            Loading()
        }

        UiState.LOADED -> {
            if (errorString == null)
                HorizontalPager(
                    state = pagerState,
                    outOfBoundsPageCount = pagerState.pageCount
                ) { semester ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Semester(plan = studyPlan!!.filter { it.semester == semester + 1 })
                    }
                }
            else
                Error(error = errorString!!)
        }
    }
}

@Composable
private fun Loading() {
    val plan = listOf(
        PlanElement().apply {
            name = "first couple"
            blockName = "b1.name.dv"
        },
        PlanElement().apply {
            name = "second couple"
            blockName = "b1.name.dv"
        },
        PlanElement().apply {
            name = "third couple"
            blockName = "b1.name.dv"
        },
        PlanElement().apply {
            name = "fourth couple"
            blockName = "b1.name.dv"
        }
    )
    Column(Modifier.verticalScroll(rememberScrollState())) {
        PlanGroup(elements = plan.subList(0, 3), title = "first group", placeholder = true)
        PlanGroup(elements = plan.subList(0, 1), title = "first group", placeholder = true)
        PlanGroup(elements = plan.subList(0, 2), title = "first group", placeholder = true)
        PlanGroup(elements = plan.subList(0, 4), title = "first group", placeholder = true)
    }
}

@Composable
private fun Error(error: String) {
    ThemeText(text = "Ошибка: $error")
}

@Composable
private fun Semester(plan: List<PlanElement>) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        val block1 = plan.filter {
            val blockName = it.blockName.split(".")[0].uppercase()
            blockName == "Б1" || blockName == "ФТД"
        }
        val block2 = plan.filter { it.blockName.split(".")[0].uppercase() == "Б2" }
        NoControl(block1.filter { it.control == PlanElement.ControlType.undefined })
        Creds(block1.filter { it.control == PlanElement.ControlType.cred })
        DifCreds(block1.filter { it.control == PlanElement.ControlType.dif_cred })
        Exams(block1.filter { it.control == PlanElement.ControlType.exam || it.control == PlanElement.ControlType.exam_kr })
        Courses(block1.filter { it.control == PlanElement.ControlType.exam_kr })
        Practices(block2)
    }
}

@Composable
private fun NoControl(creds: List<PlanElement>) {
    PlanGroup(
        creds,
        "Без контроля"
    )
}

@Composable
private fun Creds(creds: List<PlanElement>) {
    PlanGroup(
        creds,
        "Зачёты"
    )
}

@Composable
private fun DifCreds(creds: List<PlanElement>) {
    PlanGroup(
        creds,
        "Зачёты с оценкой"
    )
}

@Composable
private fun Exams(exams: List<PlanElement>) {
    PlanGroup(
        exams,
        "Экзамены"
    )
}

@Composable
private fun Courses(courses: List<PlanElement>) {
    PlanGroup(
        courses,
        "Курсовые"
    )
}

@Composable
private fun Practices(practices: List<PlanElement>) {
    PlanGroup(
        practices,
        "Практики"
    )
}

@Composable
private fun PlanGroup(elements: List<PlanElement>, title: String, placeholder: Boolean = false) {
    if (elements.isEmpty()) {
        return
    }
    var collapsed by remember {
        mutableStateOf(true)
    }
    Box(modifier = Modifier.padding(4.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.foreground.color, shape = RoundedCornerShape(10.dp))
        ) {
            val modifier = if (elements.isEmpty()) Modifier else Modifier
                .animateContentSize()
                .bounceClick {
                    if (!placeholder)
                        collapsed = !collapsed
                }
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .then(modifier)
            ) {
                Column {


                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            imageVector =
                            if (collapsed)
                                Icons.Outlined.KeyboardArrowUp
                            else
                                Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                            modifier = Modifier.placeholder(placeholder)
                        )
                        ThemeText(
                            text = title,
                            fontSize = 20.sp,
                            modifier = Modifier.placeholder(placeholder)
                        )
                        Image(
                            imageVector =
                            if (collapsed)
                                Icons.Outlined.KeyboardArrowUp
                            else
                                Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                            modifier = Modifier.placeholder(placeholder)
                        )
                    }
                    AnimatedVisibility(visible = collapsed) {
                        Column {
                            if (AppSettings.Eios.groupByBlocks) {
                                elements.groupBy { it.blockName }.forEach {
                                    PlanBlock(blockName = it.key, elements = it.value, placeholder)
                                }
                            } else {
                                elements.forEach {
                                    PlanElementView(planElement = it, placeholder)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

private fun blockNameToHuman(blockName: String): String {
    val parts = blockName.split(".")
    if (parts[0].uppercase() == "ФТД")
        return "факультативные"
    if (parts.size >= 2) {
        if (parts.size > 2 && parts[2] == "ДВ") {
            if (parts[1] == "О")
                return "общие (по выбору)"
            if (parts[1] == "В")
                return "вариативные (по выбору)"
        }
        if (parts.size > 2) {
            if (parts[1] == "О")
                return "общие (${parts[2]})"
            if (parts[1] == "В")
                return "вариативные (${parts[2]})"
        }
        if (parts[1] == "О")
            return "общие"
        if (parts[1] == "В")
            return "вариативные"
    }
    return blockName
}

@Composable
private fun PlanBlock(blockName: String, elements: List<PlanElement>, placeholder: Boolean) {
    var collapsed by remember {
        mutableStateOf(true)
    }
    Box(
        Modifier
            .fillMaxWidth()
            .bounceClick {
                if (!placeholder)
                    collapsed = !collapsed
            }) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    imageVector =
                    if (collapsed)
                        Icons.Outlined.KeyboardArrowUp
                    else
                        Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    Modifier
                        .size(15.dp)
                        .placeholder(placeholder),
                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                )
                ThemeText(
                    text = if (AppSettings.Eios.showBlockNames) blockName else blockNameToHuman(
                        blockName
                    ),
                    modifier = Modifier.placeholder(placeholder)
                )
            }
            AnimatedVisibility(visible = collapsed) {
                Column {
                    elements.forEach {
                        PlanElementView(planElement = it, placeholder)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanElementView(planElement: PlanElement, placeholder: Boolean) {
    var collapsed by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.padding(4.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.button.color, RoundedCornerShape(7.dp))
                .padding(9.dp)
                .bounceClick {
                    if (!placeholder)
                        collapsed = !collapsed
                }
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.fillMaxWidth(.6f)) {
                        ThemeText(
                            text = planElement.name,
                            modifier = Modifier.placeholder(placeholder)
                        )
                    }
                    ThemeText(
                        text = if (AppSettings.Eios.showFullHours) addHoursSuffix(planElement.hours)
                        else "${planElement.hours} ч.",
                        modifier = Modifier.placeholder(placeholder)
                    )
                }
                AnimatedVisibility(visible = collapsed) {
                    Column {
                        Divider(color = SurfaceTheme.text.color)
                        Spacer(modifier = Modifier.size(10.dp))
                        if (planElement.lecCount > 0)
                            ThemeText(text = "Лекций: ${planElement.lecCount}")
                        if (planElement.pracCount > 0)
                            ThemeText(text = "Практик: ${planElement.pracCount}")
                        if (planElement.labCount > 0)
                            ThemeText(text = "Лаб. работ: ${planElement.labCount}")
                    }
                }
            }
        }
    }
}

private fun addHoursSuffix(hours: Int): String {
    if (hours in 10..20)
        return "$hours часов"
    return "$hours ${
        when (hours.toString().last().digitToInt()) {
            1 -> "час"
            in 2..4 -> "часа"
            else -> "часов"
        }
    }"
}

