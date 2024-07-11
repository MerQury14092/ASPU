package com.merqury.aspu.services.quiz

import com.merqury.aspu.services.exam.clientWithoutRedirects
import com.merqury.aspu.services.exam.moodleCookie
import com.merqury.aspu.services.exam.sessionKey
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

fun startQuizAttempt(
    quizId: Int,
    onError: (String) -> Unit = {},
    onSuccess: (Int) -> Unit
) {
    clientWithoutRedirects.newCall(
        Request.Builder()
            .url("https://examen.agpu.net/mod/quiz/startattempt.php")
            .post(
                FormBody.Builder()
                    .add("cmid", quizId.toString())
                    .add("sesskey", sessionKey())
                    .build()
            )
            .header("cookie", moodleCookie())
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e.message ?: e.javaClass.name)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.code != 303) {
                onError("Responsed code isn't 303")
                return
            }
            if (response.headers["Location"] == null) {
                onError("Response haven't header 'Location'")
                return
            }
            onSuccess(
                response.headers["Location"]!!
                    .split("attempt=")[1]
                    .split("&")[0]
                    .toInt()
            )
        }
    })
}