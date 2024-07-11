package com.merqury.aspu.ui.navfragments.exam

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.services.exam.checkCredentials
import com.merqury.aspu.services.exam.getAllCourses
import com.merqury.aspu.services.exam.getMyCourses
import com.merqury.aspu.ui.EditableText
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.navfragments.profile.secretPreferences
import com.merqury.aspu.ui.startTopBarActivityWithActivityLink
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color

@OptIn(ExperimentalFoundationApi::class)
internal val pager = PagerState { 2 }
private var allCourses by mutableStateOf<List<ExamCourse>?>(null)

fun startExamScreen() {
    if (!secretPreferences.contains("exam-cookie")) showExamAuthModalWindow {
        appContext.startTopBarActivityWithActivityLink { header, activity ->
            ExamScreen(header = header) {
                activity!!.finish()
            }
        }
    }
    else {
        appContext.startTopBarActivityWithActivityLink { header, activity ->
            ExamScreen(header = header) {
                activity!!.finish()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamScreen(header: MutableState<@Composable () -> Unit>, finishActivity: () -> Unit) {
    header.value = {
        ExamHeader()
    }
    var credentialsStatus by remember { mutableStateOf<Boolean?>(null) }
    checkCredentials {
        credentialsStatus = it
    }
    credentialsStatus?.let { success ->
        if (!success) {
            finishActivity()
            secretPreferences.edit().remove("exam-cookie").apply()
            startExamScreen()
            appContext!!.makeToast("Время сессии истекло")
            return
        }
        HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                ExamContent(it == 1)
            }
        }
    }.also {
        if (credentialsStatus == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceTheme.background.color),
                contentAlignment = Alignment.Center
            ) {
                ThemeText(text = "Loading...")
            }
        }
    }
}


var myCourses by mutableStateOf<List<ExamCourse>?>(null)


@Composable
fun ExamContent(isMyCourseScreen: Boolean) {

    if (isMyCourseScreen) {
        if (myCourses == null) {
            Column {
                repeat(7) {
                    CourseItemPlaceholder()
                }
            }
            getMyCourses {
                myCourses = it.sortedBy { it.name }
            }
        } else {
            CourseList(list = myCourses!!, true)
        }
    } else {
        if (allCourses == null) {
            getAllCourses { courses ->
                allCourses = courses.sortedBy { it.name }
            }
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                repeat(25) {
                    CourseItemPlaceholder()
                }
            }
        } else {
            CourseList(list = allCourses!!, false)
        }
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun CourseList(list: List<ExamCourse>, isMyCourseScreen: Boolean) {
    var filter by remember {
        mutableStateOf("")
    }
    LazyColumn {
        if (list.size > 10)
            item {
                Spacer(modifier = Modifier.size(45.dp))
            }
        items(list.filter { it.name.lowercase().contains(filter.lowercase()) }) {
            CourseItem(item = it, isMyCourseScreen)
        }
    }

    if (list.size > 10)
        Box(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        SurfaceTheme.foreground.color,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .border(
                        2.dp,
                        color = SurfaceTheme.placeholder_secondary.color,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .height(40.dp)
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(10.dp))
                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                    EditableText(
                        value = filter,
                        onChange = { filter = it },
                        placeholder = "Введите для поиска"
                    )
                }
            }
        }
}