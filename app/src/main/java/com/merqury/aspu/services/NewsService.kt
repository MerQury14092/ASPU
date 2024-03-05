package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.requestQueue
import org.json.JSONObject

fun getNews(
    faculty: NewsCategoryEnum,
    pageNumber: Int,
    newsResponse: MutableState<JSONObject>,
    countPages: MutableIntState,
    newsLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>,
    responseText: MutableState<String>
) {
    newsLoaded.value = false
    var url = "https://agpu.merqury.fun/api/news"
    if (faculty != NewsCategoryEnum.agpu)
        url = "$url/${faculty.name}"
    url = "$url?page=$pageNumber"
    val request = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val convertedResponse = EncodingConverter.translateISO8859_1toUTF_8(response)
            newsResponse.value = JSONObject(convertedResponse)
            countPages.intValue = newsResponse.value.getInt("countPages")
            newsLoaded.value = true
            success.value = true
        },
        {
            success.value = false
            newsLoaded.value = true
            if(it.javaClass == NoConnectionError::class.java)
                responseText.value = "Нет подключения к интернету!"
            else if(it.javaClass == ServerError::class.java)
                if (it.networkResponse.statusCode == 502)
                    responseText.value = "На сервере ведутся плановые технические работы."
                else
                    responseText.value = "Неизвестная ошибка! Отчёт был отправлен разработчику."
        }
    )
    requestQueue!!.add(request)
}

fun getNewsArticle(
    faculty: NewsCategoryEnum,
    id: Int,
    articleResponse: MutableState<JSONObject>,
    articleLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>
) {
    articleLoaded.value = false
    val url = "https://agpu.merqury.fun/api/news/${faculty.name}/$id"
    val request = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val convertedResponse = EncodingConverter.translateISO8859_1toUTF_8(response)
            articleResponse.value = JSONObject(convertedResponse)
            articleLoaded.value = true
            success.value = true
        },
        {
            success.value = false
            articleLoaded.value = true
            Log.d("network-error", "ERROR")
        }
    )
    requestQueue!!.add(request)
}