package com.merqury.aspu.ui.navfragments.quiz

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merqury.aspu.services.quiz.getQuestion
import com.merqury.aspu.services.quiz.getQuestionsCount
import com.merqury.aspu.services.quiz.models.QuestionModel
import com.merqury.aspu.services.quiz.startQuizAttempt
import com.merqury.aspu.ui.theme.ThemeText
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalFoundationApi::class)
internal var pagerState = PagerState { 0 }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizScreen(
    header: MutableState<@Composable () -> Unit>,
    quizId: Int,
    finishActivity: () -> Unit
) {
    var questionsCount by remember {
        mutableIntStateOf(-1)
    }
    var attemptId by remember {
        mutableIntStateOf(-1)
    }
    if (attemptId == -1) {
        startQuizAttempt(
            quizId,
            {}
        ) {
            attemptId = it
        }
        QuestionPlaceholderScreen()
    } else {
        if (questionsCount == -1) {
            getQuestionsCount(
                attemptId,
                quizId,
                {}
            ) {
                questionsCount = it
            }
            QuestionPlaceholderScreen()
        } else {
            questionAnswers = (0..<questionsCount).associate {
                it to ConcurrentHashMap()
            }
            pagerState = rememberPagerState { questionsCount }
            header.value = {
                QuizHeader(
                    quizId,
                    attemptId,
                    pagerState = pagerState,
                    finishActivity
                )
            }
            HorizontalPager(
                state = pagerState,
                outOfBoundsPageCount = 4
            ) {
                QuizContent(quizId = quizId, attemptId = attemptId, page = it, questionsCount)
            }
        }
    }
}

@Composable
fun QuestionPlaceholderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            QuestionPlaceholder()
        }
    }
}

internal var questionAnswers: Map<Int, MutableMap<String, String>>? = null

@Composable
fun QuizContent(
    quizId: Int,
    attemptId: Int,
    page: Int,
    pageCount: Int
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (question == null)
                QuestionPlaceholder()
            else {
                if (question!!.images.isNotEmpty()) {
                    ThemeText(
                        text = "⃰примечание: для зумирования вы можете кликнуть по картинке",
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                    if (question!!.images.size > 1) {
                        ThemeText(
                            text = "Это только одна картинка из многих, остальные можно посмотреть, кликнув по этой и листая",
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Question(
                    questionModel = question!!,
                    questionAnswers!!
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            ThemeText(text = "${page + 1} из $pageCount", fontStyle = FontStyle.Italic)
        }
    }
}