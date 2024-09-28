package com.merqury.aspu.ui.screens.quiz

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.quiz.postAnswers
import com.merqury.aspu.ui.showLoadingModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizHeader(
    quizInt: Int,
    attemptId: Int,
    pagerState: PagerState,
    finishActivity: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Button(
                onClick = {
                    if (pagerState.currentPage > 0)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage - 1
                            )
                        }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceTheme.button.color
                )
            ) {
                ThemeText(text = "Назад", fontSize = 11.sp)
            }
            Button(
                onClick = {
                    if (pagerState.currentPage < pagerState.pageCount)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceTheme.button.color
                )
            ) {
                ThemeText(text = "Вперёд", fontSize = 11.sp)
            }
        }
        Button(
            onClick = {
                val loadingTitle = mutableStateOf("Отправляю ответы")
                val loadingSuccess = mutableStateOf<Boolean?>(null)
                var errorStatus = 0
                showLoadingModalWindow(loadingTitle, loadingSuccess) {
                    if(loadingSuccess.value!!){
                        finishActivity()
                        showLastResult(quizInt)
                    } else {
                        if(errorStatus == 2)
                            finishActivity()
                    }
                }
                finishAttempt(
                    quizInt,
                    attemptId,
                    {
                        loadingTitle.value = "Завершаю попытку"
                    },
                    {
                        loadingTitle.value = "Не получилось отправить ответы, попробуйте еще раз"
                        loadingSuccess.value = false
                        errorStatus = 1
                    },
                    {
                        loadingTitle.value = "Успешно! Окно сейчас закроется"
                        loadingSuccess.value = true
                    },
                    {
                        loadingTitle.value = "Ошибка завершения, хотя ответы были сохранены. " +
                                "Пожалуйста, ввойдите на сайт, нажмите продолжить попытку и завершите"
                        loadingSuccess.value = false
                        errorStatus = 2
                    }
                )
            }, colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceTheme.button.color
            )
        ) {
            ThemeText(text = "Завершить попытку", fontSize = 11.sp)
        }
    }
}

private fun finishAttempt(
    quizInt: Int,
    attemptId: Int,
    onSuccessPostAnswers: () -> Unit,
    onErrorPostAnswers: () -> Unit,
    onSuccessFinishAttempt: () -> Unit,
    onErrorFinishAttempt: () -> Unit
){
    postAnswers(
        quizInt,
        attemptId,
        questionAnswers!!,
        {
            onErrorPostAnswers()
        }
    ) {
        onSuccessPostAnswers()
        com.merqury.aspu.services.quiz.finishAttempt(
            quizInt,
            attemptId,
            {
                onErrorFinishAttempt()
            }
        ) {
            onSuccessFinishAttempt()
        }
    }
}