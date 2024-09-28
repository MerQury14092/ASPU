package com.merqury.aspu.ui.screens.marks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.merqury.aspu.services.marks.models.MarksResponse
import com.merqury.aspu.services.studyplan.PlanElement
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.theme.ThemeText

var marksContent by mutableStateOf<MarksResponse?>(null)
var marksLoadError by mutableStateOf<String?>(null)
var plan = ArrayList<PlanElement>()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarksScreen(header: MutableState<@Composable () -> Unit>) {

    val pagerState = rememberPagerState {
        if (marksContent == null)
            return@rememberPagerState 1;
        val lastSemester = plan.maxOf { it.semester }
        return@rememberPagerState lastSemester;
    }

    header.value = {
        TitleHeader(title = "${(pagerState.currentPage) / 2 + 1} курс, ${pagerState.currentPage + 1} семестр")
    }

    if (marksLoadError == null)
        HorizontalPager(state = pagerState) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                MarksBlock(type = ControlType.cred, it + 1)
                MarksBlock(type = ControlType.exam, it + 1)
            }
        }
    else
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            ThemeText(text = marksLoadError!!)
        }
}