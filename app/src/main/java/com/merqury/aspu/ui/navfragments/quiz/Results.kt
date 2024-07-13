package com.merqury.aspu.ui.navfragments.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.ExamAuthorizedStringRequest
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim
import org.jsoup.Jsoup

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
        requestQueue!!.add(ExamAuthorizedStringRequest(
            Method.GET,
            "https://examen.agpu.net/mod/quiz/view.php?id=$quizId",
            {
                val results = Jsoup.parse(it)
                    .select(".quizattemptsummary tr")
                    .filter { resultElement ->
                        resultElement.text().lowercase().contains("Заверш".lowercase())
                    }
                    .toList()

                result =
                    if (results.isEmpty())
                        null
                    else results.last().select("td").dropLast(1).last().text().replace(",", ".")
                loaded = true
            },
            {
                success = false
            }
        ))
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