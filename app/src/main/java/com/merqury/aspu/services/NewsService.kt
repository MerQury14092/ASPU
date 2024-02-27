package com.merqury.aspu.services

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.requestQueue
import org.json.JSONObject

fun getNews(
    faculty: NewsCategoryEnum,
    pageNumber: Int,
    newsResponse: MutableState<JSONObject>,
    countPages: MutableIntState,
    newsLoaded: MutableState<Boolean>
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
        },
        {}
    )
    requestQueue!!.add(request)
}

fun getNewsArticle(
    faculty: NewsCategoryEnum,
    id: Int,
    articleResponse: MutableState<JSONObject>,
    articleLoaded: MutableState<Boolean>
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
        },
        {}
    )
    requestQueue!!.add(request)
}