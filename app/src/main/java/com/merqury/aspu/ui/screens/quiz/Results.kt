package com.merqury.aspu.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merqury.aspu.services.quiz.getLastResult
import com.merqury.aspu.services.quiz.models.ResultModel
import com.merqury.aspu.ui.navfragments.timetable.prettyDate
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim

fun showLastResult(
    quizId: Int
) {

    showSimpleModalWindow(
        containerColor = SurfaceTheme.background.colorWithoutAnim
    ) {
        var loaded by remember {
            mutableStateOf(false)
        }
        var success by remember {
            mutableStateOf(true)
        }
        var result by remember {
            mutableStateOf<String?>(null)
        }

        getLastResult(quizId, {
            loaded = true
            success = false
        }){
            loaded = true
            success = true
            result = it
        }

        Box(modifier = Modifier.padding(30.dp), contentAlignment = Alignment.Center) {
            if (loaded)
                ThemeText(
                    text =
                    if(!success)
                        "Произошла ошибка"
                    else if (result == null)
                        "Результата нет"
                    else {
                        val count = result!!.toDouble()
                        val presumablyMark =
                            if (count >= 86) 5
                            else if (count >= 71) 4
                            else if (count >= 56) 3
                            else 2
                        "Ваш результат: $count. Это предположительно: $presumablyMark"
                    }
                )
            else
                CircularProgressIndicator(color = SurfaceTheme.text.color)
        }
    }

}

fun showResults(
    results: List<ResultModel>
) {
    showSimpleModalWindow (
        containerColor = SurfaceTheme.background.colorWithoutAnim
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.92f)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                val sorted = results.sortedBy { it.id }
                if(sorted.isEmpty()){
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceTheme.foreground.color
                        ),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ThemeText(text = "Результатов пока еще нет")
                        }
                    }
                }
                sorted.forEach {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceTheme.foreground.color
                        ),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ThemeText(text = "${it.id}: ${it.result}",)
                            ThemeText(text = "${prettyDate(it.date)} в ${it.time}")
                        }
                    }
                }
            }
        }
    }
}