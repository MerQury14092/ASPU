package com.merqury.aspu.services.exam

import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.navfragments.profile.secretPreferences
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException

const val examLoginUrl = "https://examen.agpu.net/login/index.php";
val client = OkHttpClient()
val clientWithoutRedirects = OkHttpClient.Builder()
    .followRedirects(false)
    .followSslRedirects(false)
    .build()

fun loginExamPlatform(
    username: String,
    password: String,
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    var cookie: String
    val request = Request.Builder()
        .url(examLoginUrl)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError("Network Error")
        }

        override fun onResponse(call: Call, response: Response) {
            cookie = response.headers["Set-Cookie"]!!.split(";")[0]

            val doc = Jsoup.parse(response.body!!.string())
            val loginToken = doc.getElementsByAttributeValue("name", "logintoken").attr("value")


            clientWithoutRedirects.newCall(
                Request.Builder()
                    .url(examLoginUrl)
                    .post(
                        FormBody.Builder()
                            .add("logintoken", loginToken)
                            .add("username", username)
                            .add("password", password)
                            .build()
                    )
                    .header("Cookie", cookie)
                    .build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError("Network Error")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.headers["Set-Cookie"] != null) {
                        var moodleSession: String = ""
                        var moodleId: String = ""
                        response.headers.forEach {
                            if (it.first.lowercase() == "set-cookie") {
                                val pair = it.second.split(";")[0].split("=")
                                if (pair[0].lowercase() == "moodlesession")
                                    moodleSession = pair[1]
                                if (pair[0].lowercase() == "moodleid1_")
                                    moodleId = pair[1]
                            }
                        }
                        cookie = "MoodleSession=$moodleSession; MOODLEID1_=$moodleId"
                        secretPreferences.edit().putString("exam-cookie", cookie).apply()
                        getMoodleSessionKey(onError) {
                            onSuccess()
                        }
                    } else {
                        onError("Bad Credentials")
                    }
                }
            })
        }
    })
}

fun sessionKey(): String {
    return secretPreferences.getString("moodle-sesskey", null)
        ?: throw RuntimeException("Trying to get session key, when it is null")
}

fun moodleCookie(): String {
    return secretPreferences.getString("exam-cookie", null)
        ?: throw RuntimeException("Trying to get exam cookie, when it is null")
}

private fun getMoodleSessionKey(onError: (String) -> Unit = {}, onSuccess: () -> Unit) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net",
        { html ->
            val sessionKey = html.split("sesskey\":\"")[1].split("\"")[0]
            secretPreferences.edit().putString("moodle-sesskey", sessionKey).apply();
            onSuccess()
        },
        {
            onError(it.message?:"Unknown error")
        }
    ))
}

open class ExamAuthorizedStringRequest(
    method: Int,
    url: String?,
    listener: com.android.volley.Response.Listener<String>?,
    errorListener: com.android.volley.Response.ErrorListener?
) : StringRequest(method, url, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Cookie"] = secretPreferences.getString("exam-cookie", null)!!
        return headers
    }
}

fun checkCredentials(onResult: (Boolean) -> Unit) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/",
        {
            if (it.contains("О пользователе"))
                onResult(true)
            else
                onResult(false)
        },
        {
            onResult(false)
        }
    ))
}

