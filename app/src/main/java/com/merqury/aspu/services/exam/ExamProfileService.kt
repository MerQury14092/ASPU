package com.merqury.aspu.services.exam

import com.android.volley.Request
import com.merqury.aspu.requestQueue
import org.jsoup.Jsoup

fun getProfilePhotoUrl(onSuccess: (String) -> Unit) {
    requestQueue!!.add(ExamAuthorizedStringRequest(
        Request.Method.GET,
        "https://examen.agpu.net/user/profile.php",
        {
            val document = Jsoup.parse(it)
            val pictureElement = document.select("img.userpicture").first()
            if (pictureElement != null)
                onSuccess(pictureElement.attr("src"))
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