package com.merqury.aspu.services.exam

import com.android.volley.Request.Method
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.exam.myCourses
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import kotlin.random.Random

fun getAllCourses(onError: (String) -> Unit = {}, onSuccess: (List<ExamCourse>) -> Unit) {
    val res = ArrayList<ExamCourse>()

    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/",
        { response ->
            val document = Jsoup.parse(response)
            parseCategories(document, res, onError) {
                onSuccess(res)
            }
        },
        {
            onError(it.message ?: "Unknown error")
        }
    ).apply { tag = "load courses" })
}

private const val myCoursesRequestBody = """
    [
    {
        "index": 0,
        "methodname": "core_course_get_enrolled_courses_by_timeline_classification",
        "args": {
            "offset": 0,
            "limit": 0,
            "classification": "all",
            "sort": "fullname",
            "customfieldname": "",
            "customfieldvalue": ""
        }
    }
]
"""

fun getMyCourses(onError: (String) -> Unit = {}, onSuccess: (List<ExamCourse>) -> Unit) {
    requestQueue!!.add(object : ExamAuthorizedStringRequest(
        Method.POST,
        "https://examen.agpu.net/lib/ajax/service.php?sesskey=${sessionKey()}",
        { response ->
            async {
                MyCourses.fromJson(response)[0].data?.courses?.let { course ->
                    onSuccess(
                        course.map {
                            ExamCourse(
                                it.fullname!!,
                                it.id!!.toInt()
                            )
                        }
                    )
                }
            }
        },
        {
            onError(it.message ?: "Unknown error")
        }
    ) {
        override fun getBodyContentType(): String {
            return "application/json"
        }

        override fun getBody(): ByteArray {
            return myCoursesRequestBody.toByteArray()
        }
    })
}

private fun parseCategories(
    document: Element,
    result: ArrayList<ExamCourse>,
    onError: (String) -> Unit,
    closure: () -> Unit
) {
    val courses = document.select(".coursename a")
    courses.forEach { link ->
        result.add(
            ExamCourse(
                link.text(),
                link.attr("href").split("=").last().toInt()
            )
        )
    }
    val categories = document.select(".category a")
    if (categories.isEmpty()) {
        closure()
        return
    }
    val categoriesRemainder = ArrayList(categories.toList())
    categories.forEach { category ->
        requestQueue!!.add(ExamAuthorizedStringRequest(
            Method.GET,
            category.attr("href") + "&perpage=${Int.MAX_VALUE}",
            {
                parseCategories(Jsoup.parse(it), result, onError) {
                    categoriesRemainder.remove(category)
                    if (categoriesRemainder.isEmpty())
                        closure()
                }
            },
            {
                onError(it.message ?: "Unknown error")
            }
        ).apply { tag = "load courses" })
    }
}

private val loginTries = ArrayList<Long>()

fun cancelAllLoginTries() {
    loginTries.clear()
}

fun loginToCourse(
    id: Int,
    password: String,
    onError: (String) -> Unit = {},
    onRightPassword: (String) -> Unit = {},
    onSuccess: (Boolean) -> Unit
) {
    loginToCourseWithInstance(
        id,
        password,
        onError,
        onRightPassword,
    ) { res, _ ->
        onSuccess(res)
    }
}

fun loginToCourseWithInstance(
    id: Int,
    password: String,
    onError: (String) -> Unit = {},
    onRightPassword: (String) -> Unit = {},
    onSuccess: (Boolean, Int) -> Unit
) {
    val tryId = Random.nextLong()
    loginTries.add(tryId)
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/enrol/index.php?id=$id",
        { html ->
            val instance = Jsoup.parse(html)
                .getElementsByAttributeValue("name", "instance")
                .first()
                ?.attr("value")
                .toString()

            client.newCall(
                Request.Builder()
                    .url("https://examen.agpu.net/enrol/index.php")
                    .header("cookie", moodleCookie())
                    .post(
                        FormBody.Builder()
                            .add("id", id.toString())
                            .add("instance", instance)
                            .add("sesskey", sessionKey())
                            .add("_qf__${instance}_enrol_self_enrol_form", "1")
                            .add("enrolpassword", password)
                            .build()
                    )
                    .build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError(e.message ?: e.javaClass.name)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body!!.string()
                    if (body.lowercase()
                            .contains("Вы записаны на курс.".lowercase())
                    ) {
                        myCourses = null
                        if (loginTries.contains(tryId))
                            onSuccess(true, instance.toInt())
                        else
                            onRightPassword(password)
                    } else if (body.lowercase()
                            .contains("Неверное кодовое слово, попробуйте еще раз".lowercase()) && loginTries.contains(
                            tryId
                        )
                    )
                        onSuccess(false, instance.toInt())
                    else
                        onError("Неизвестный ответ (ключевое слово)")
                }

            })
        },
        {}
    ))
}

fun logoutCourseById(
    courseId: Int,
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Method.GET,
        "https://examen.agpu.net/course/view.php?id=$courseId",
        {
            val enrolId = it.split("enrolid=")[1].split("\"")[0].toInt()
            logoutCourse(enrolId, onError, onSuccess)
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}

fun logoutCourse(
    enrolId: Int,
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit
) {
    client.newCall(
        Request.Builder()
            .url("https://examen.agpu.net/enrol/self/unenrolself.php")
            .header("cookie", moodleCookie())
            .post(
                FormBody.Builder()
                    .add("enrolid", enrolId.toString())
                    .add("confirm", "1")
                    .add("sesskey", sessionKey())
                    .build()
            )
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError(e.message ?: e.javaClass.name)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.body!!.string().lowercase().contains("Вы отчислены из курса".lowercase()))
                onSuccess()
            else
                onError("Unknown error on logout course")
        }
    })
}

fun checkPassword(
    id: Int,
    password: String,
    onError: (String) -> Unit = {},
    onSuccess: (Boolean) -> Unit
) {
    loginToCourseWithInstance(
        id,
        password,
        onError
    ) { success, enrolId ->
        if (success) {
            logoutCourse(
                enrolId,
                onError
            ) {
                onSuccess(true)
            }
        } else {
            onSuccess(false)
        }
    }
}