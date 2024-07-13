package com.merqury.aspu.services.quiz

import com.merqury.aspu.services.exam.client
import com.merqury.aspu.services.exam.moodleCookie
import com.merqury.aspu.services.exam.sessionKey
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@Volatile
private var bodyBuilder: FormBody.Builder = FormBody.Builder()

fun postAnswers(
    quizId: Int,
    attemptId: Int,
    questionsAnswers: Map<Int, Map<String, String>>,
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit
) {
    var questionRemainder = questionsAnswers.size
    questionsAnswers.forEach { (questionNumber, answers) ->
        bodyBuilder = FormBody.Builder()
            .add("attempt", attemptId.toString())
            .add("sesskey", sessionKey())
        getQuestion(
            questionNumber,
            quizId,
            attemptId,
            onError
        ) {
            bodyBuilder = bodyBuilder.add(it.sequenceId, it.sequenceValue)
            answers.forEach { (answerName, answerValue) ->
                bodyBuilder = bodyBuilder.add(answerName, answerValue)
            }
            questionRemainder-=1
            if(questionRemainder == 0) {
                client.newCall(
                    Request.Builder()
                        .url("https://examen.agpu.net/mod/quiz/processattempt.php?cmid=$quizId")
                        .header("cookie", moodleCookie())
                        .post(
                            bodyBuilder.build()
                        )
                        .build()
                ).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onError("Error")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.body!!.string().lowercase()
                                .contains("Вы ввели данные в неправильном порядке".lowercase())
                        ){
                            onError("sequence id error")
                            return
                        }
                        onSuccess()
                    }
                })
            }

        }


    }
}

fun finishAttempt(
    quizId: Int,
    attemptId: Int,
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit
) {
    client.newCall(
        Request.Builder()
            .url("https://examen.agpu.net/mod/quiz/processattempt.php")
            .header("cookie", moodleCookie())
            .post(
                FormBody.Builder()
                    .add("sesskey", sessionKey())
                    .add("attempt", attemptId.toString())
                    .add("finishattempt", "1")
                    .add("cmid", quizId.toString())
                    .build()
            )
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError("Error while finishing attempt")
        }

        override fun onResponse(call: Call, response: Response) {
            onSuccess()
        }
    })
}