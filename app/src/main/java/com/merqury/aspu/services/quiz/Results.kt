package com.merqury.aspu.services.quiz

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.ExamAuthorizedStringRequest
import com.merqury.aspu.services.quiz.models.ResultModel
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun getLastResult(
    quizId: Int,
    onError: (String) -> Unit,
    onSuccess: (String?) -> Unit
){
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

            onSuccess(
                if (results.isEmpty())
                    null
                else results.last().select("td").dropLast(1).last().text().replace(",", ".")
            )
        },
        {
            onError(it.message?:it.javaClass.name)
        }
    ))
}

fun getResults(
    quizId: Int,
    onError: (String) -> Unit,
    onSuccess: (List<ResultModel>) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/mod/quiz/view.php?id=$quizId",
        { html ->
            val results = Jsoup.parse(html)
            .select(".quizattemptsummary tr")
            .filter { resultElement ->
                resultElement.text().lowercase().contains("Заверш".lowercase())
            }
            .toList()

            onSuccess(
                if (results.isEmpty())
                    listOf()
                else results
                    .map {
                        val td = it
                            .select("td")

                        val id = td.first()!!.text().toInt()
                        val result = td.dropLast(1).last().text().replace(",",".")
                        val datetime = LocalDateTime.parse(
                            td.select(".statedetails").text().replace("Отправлено ", ""),
                            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy, HH:mm", Locale("ru"))
                        )
                        ResultModel(
                            id,
                            result.toDouble(),
                            datetime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            datetime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }.toList()
            )
        },
        {
            onError(it.message?:it.javaClass.name)
        }
    ))
}