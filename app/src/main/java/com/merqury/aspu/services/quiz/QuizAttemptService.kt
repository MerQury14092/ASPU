package com.merqury.aspu.services.quiz

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.ExamAuthorizedStringRequest
import com.merqury.aspu.services.quiz.models.AnswerModel
import com.merqury.aspu.services.quiz.models.AnswerType
import com.merqury.aspu.services.quiz.models.QuestionModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun getQuestionsCount(
    attemptId: Int,
    quizId: Int,
    onError: (String) -> Unit = {},
    onSuccess: (Int) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/mod/quiz/attempt.php?attempt=$attemptId&cmid=$quizId&page=0",
        {
            onSuccess(
                Jsoup.parse(it)
                    .getElementsByAttribute("data-quiz-page")
                    .count()
            )
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}

fun getQuestion(
    questionId: Int,
    quizId: Int,
    attemptId: Int,
    onError: (String) -> Unit = {},
    onSuccess: (QuestionModel) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/mod/quiz/attempt.php?attempt=$attemptId&cmid=$quizId",
        { quizHtml ->
            val questionsElements = Jsoup.parse(quizHtml)
                .select(".que")

            val paginated = questionsElements.size == 1

            if (paginated) {
                if (questionId == 0)
                    onSuccess(parseQuestionElement(questionsElements[0]))
                else {
                    getQuestionPage(
                        questionId,
                        quizId,
                        attemptId,
                        onError
                    ) {
                        onSuccess(
                            parseQuestionElement(
                                it.select(".que")
                                    .first()!!
                            )
                        )
                    }
                }
            } else {
                onSuccess(parseQuestionElement(questionsElements[questionId]))
            }
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}

private fun getQuestionPage(
    questionId: Int,
    quizId: Int,
    attemptId: Int,
    onError: (String) -> Unit = {},
    onSuccess: (Element) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/mod/quiz/attempt.php?attempt=$attemptId&cmid=$quizId&page=$questionId",
        {
            onSuccess(Jsoup.parse(it))
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}

fun parseQuestionElement(element: Element): QuestionModel {
    val el = element.select(".content").first()!!
    val sequenceCheck = el
        .select("input[type=hidden][name*=sequencecheck]")

    val sequenceCheckName = sequenceCheck.attr("name")
    val sequenceCheckValue = sequenceCheck.attr("value")
    val qText = el
        .select(".qtext")
        .text()
    val images = el
        .select("img")
        .map { img ->
            img.attr("src")
        }.toList()

    val answers = ArrayList(el
        .select(".answer")
        .first()!!
        .select("div.r0, div.r1")
        .map {
            val input = it.select("input[type*=o]").first()!!

            val inputType = when (input.attr("type")) {
                "radio" -> AnswerType.radio
                "checkbox" -> AnswerType.check
                else -> throw RuntimeException("Unknown answer type: $input")
            }
            val inputName = input.attr("name")
            val inputValue = input.attr("value")
            val text = it.select(".flex-fill").text()
            return@map AnswerModel(
                text,
                inputType,
                inputName,
                inputValue
            )
        }.toList()
    )

    el.select(".answer")
        .first()!!
        .select("input[type=text][name*=answer]")
        .forEach {
            answers.add(
                AnswerModel(
                    "",
                    AnswerType.text,
                    it.attr("name"),
                    "none"
                )
            )
        }


    return QuestionModel(
        qText,
        sequenceCheckName,
        sequenceCheckValue,
        images,
        answers
    )
}