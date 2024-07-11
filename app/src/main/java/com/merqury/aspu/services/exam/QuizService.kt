package com.merqury.aspu.services.exam

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.models.ExamQuiz
import org.jsoup.Jsoup

fun getQuizzes(
    courseId: Int,
    onError: (String) -> Unit = {},
    onSuccess: (List<ExamQuiz>) -> Unit
){
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/course/view.php?id=$courseId",
        {
            val result = Jsoup.parse(it).select(".quiz .activityname a")
                .map { quiz ->
                    val id = quiz.attr("href").split("id=")[1].toInt()
                    val name = quiz.select(".instancename").text()
                        .replace(" Тест", "")
                    return@map ExamQuiz(
                        id,
                        name,
                        courseId
                    )
                }
            onSuccess(result)
        },
        {
            onError(it.message?:it.javaClass.name)
        }
    ))
}