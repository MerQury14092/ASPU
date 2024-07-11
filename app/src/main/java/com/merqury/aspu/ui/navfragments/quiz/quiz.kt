package com.merqury.aspu.ui.navfragments.quiz

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.merqury.aspu.services.quiz.getQuestion
import com.merqury.aspu.services.quiz.getQuestionsCount
import com.merqury.aspu.services.quiz.models.QuestionModel
import com.merqury.aspu.services.quiz.startQuizAttempt
import com.merqury.aspu.ui.theme.ThemeText

@OptIn(ExperimentalFoundationApi::class)
internal var pagerState = PagerState { 0 }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizScreen(header: MutableState<@Composable () -> Unit>, quizId: Int) {
    var questionsCount by remember {
        mutableIntStateOf(-1)
    }
    var attemptId by remember {
        mutableIntStateOf(-1)
    }
    if (attemptId == -1)
        startQuizAttempt(
            quizId,
            {}
        ) {
            attemptId = it
        }
    else {
        if (questionsCount == -1)
            getQuestionsCount(
                attemptId,
                quizId,
                {}
            ) {
                questionsCount = it
            }
        else {
            pagerState = rememberPagerState { questionsCount }
            HorizontalPager(state = pagerState) {
                QuizContent(quizId = quizId, attemptId = attemptId, page = it)
            }
        }
    }
}

@Composable
fun QuizContent(
    quizId: Int,
    attemptId: Int,
    page: Int
) {
    var question by remember {
        mutableStateOf<QuestionModel?>(null)
    }
    getQuestion(
        page,
        quizId,
        attemptId,
        {}
    ) {
        question = it
    }
    Box(modifier = Modifier.fillMaxSize()){
        if(question == null)
            ThemeText(text = "loading")
        else {
            Question(questionModel = question!!)
        }
    }
}