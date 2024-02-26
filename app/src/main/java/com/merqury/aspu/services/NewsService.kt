package com.merqury.aspu.services

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.mainContext
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
    if (faculty.name.lowercase() != "general")
        url = "$url/${faculty.name.lowercase()}"
    url = "$url?page=$pageNumber"
    val queue = Volley.newRequestQueue(mainContext)
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
    queue.add(request)
}

fun getNewsArticle(
    faculty: NewsCategoryEnum,
    id: Int,
    articleResponse: MutableState<JSONObject>
) {

}