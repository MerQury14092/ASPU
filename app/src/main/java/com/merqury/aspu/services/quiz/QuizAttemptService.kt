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
    val content = element.select(".content").first()!!
    val sequenceCheck = content
        .select("input[type=hidden][name*=sequencecheck]")

    val sequenceCheckName = sequenceCheck.attr("name")
    val sequenceCheckValue = sequenceCheck.attr("value")
    val qText = content
        .select(".qtext")
        .text()
        .replace(Regex("Ответ Вопрос \\d+"), " [ответ] ")
    val images = content
        .select("img")
        .map { img ->
            img.attr("src")
        }.toList()

    val answers = ArrayList(content
        .select(".r0, .r1")
        .map {
            if(it.select("select").isNotEmpty()){
                return@map AnswerModel(
                    it.select(".text").text(),
                    AnswerType.select,
                    it.select("select").attr("name"),
                    null,
                    it.select("select option")
                        .associate { option ->
                            option.text() to option.attr("value")
                        }
                )
            }

            val input = it.select("input[type*=o]").first()!!

            val inputType = when (input.attr("type")) {
                "radio" -> AnswerType.radio
                "checkbox" -> AnswerType.check
                else -> throw RuntimeException("Unknown answer type: $input")
            }
            val inputName = input.attr("name")
            val inputValue = input.attr("value")
            val text = it.select(".flex-fill, label[for*=answer]").text()
            return@map AnswerModel(
                text,
                inputType,
                inputName,
                inputValue,
                null
            )
        }.toList()
    )

    content.select("input[type=text][name*=answer]")
        .forEach {
            answers.add(
                AnswerModel(
                    "",
                    AnswerType.text,
                    it.attr("name"),
                    null,
                    null
                )
            )
        }


    return QuestionModel(
        element.select(".qno").first()!!.text().toInt()-1,
        qText,
        sequenceCheckName,
        sequenceCheckValue,
        images,
        answers
    )
}