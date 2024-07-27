package com.merqury.aspu.services.exam

import com.android.volley.Request
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.services.exam.models.ExamUserModel
import com.merqury.aspu.ui.async
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun getProfilePhotoUrl(onSuccess: (String) -> Unit) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Request.Method.GET,
        "https://examen.agpu.net/user/profile.php",
        {
            val document = Jsoup.parse(it)
            val pictureElement = document.select("img.userpicture").first()
            if (pictureElement != null)
                onSuccess(pictureElement.attr("src").replace("/f2", "/f1"))
            else
                onSuccess("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAd5avdba8EiOZH8lmV3XshrXx7dKRZvhx-A&s")
        },
        {}
    ))
}

fun getNameOfUser(onSuccess: (String) -> Unit) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Request.Method.GET,
        "https://examen.agpu.net/user/profile.php",
        {
            val document = Jsoup.parse(it)
            val nameElement = document.select("div.page-header-headings h1.h2").first()
            if (nameElement != null) {
                onSuccess(nameElement.text())
            }
        },
        {}
    ))
}

fun getUserInfo(
    onError: (String) -> Unit,
    onSuccess: (ExamUserModel) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Request.Method.GET,
        "https://examen.agpu.net/user/profile.php?showallcourses=1",
        { html ->
            async {
                onSuccess(parseHtmlProfile(html))
            }
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}

fun getUserInfo(
    userId: Int,
    onError: (String) -> Unit,
    onSuccess: (ExamUserModel) -> Unit
) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Request.Method.GET,
        "https://examen.agpu.net/user/profile.php?id=$userId&showallcourses=1",
        { html ->
            async {
                onSuccess(parseHtmlProfile(html))
            }
        },
        {
            onError(it.message ?: it.javaClass.name)
        }
    ))
}

private fun parseHtmlProfile(html: String): ExamUserModel {
    val document = Jsoup.parse(html)
    val nameElements = document.select("div.page-header-headings h1.h2").first()?.text()
        ?.split(" ")!!
    val firstName = nameElements[0]
    val lastName = nameElements[1]
    val userPicture = document.select(".userpicture").drop(1).find { true }
    val res = ExamUserModel(
        firstName,
        lastName,
        userPicture?.attr("src")?.replace("/f2", "/f1"),
        null,
        null,
        null,
        null,
        listOf()
    )

    document.select(".profile_tree dl")
        .forEach { dl ->
            when (dl.select("dt").text()) {
                "Адрес электронной почты" -> res.email = dl.select("dd a").text()
                "Страна" -> res.country = dl.select("dd").text()
                "Город" -> res.city = dl.select("dd").text()
                "Участник курсов" -> {
                    res.courses = dl.select("dd li")
                        .map {
                            ExamCourse(
                                it.text(),
                                it.select("a").attr("href")
                                    .split("course=")[1]
                                    .split("&")[0]
                                    .toInt()
                            )
                        }.toList()
                }

                "Последний доступ к сайту" -> {
                    res.lastLogin = LocalDateTime.parse(
                        dl.select("dd").text().split("\u00A0")[0]
                            .replace(Regex(" \\(.*\\)"), ""),
                        DateTimeFormatter.ofPattern(
                            "EEEE, d MMMM yyyy, HH:mm",
                            Locale("ru")
                        )
                    )
                }
            }
        }
    return res
}