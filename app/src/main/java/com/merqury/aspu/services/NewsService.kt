package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.apiDomain
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.news.currentPage
import com.merqury.aspu.ui.navfragments.news.selectedFaculty
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import org.json.JSONObject
import java.util.concurrent.TimeUnit

fun getNews(
    faculty: NewsCategoryEnum,
    pageNumber: Int,
    newsResponse: MutableState<JSONObject>,
    countPages: MutableIntState,
    newsLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>,
    responseText: MutableState<String>
) {
    val timeCache = settingsPreferences.getLong("timeCache", TimeUnit.HOURS.toSeconds(3))
    if (timeCache != 0L && cache.getString("${faculty.name} $pageNumber", "") != "") {
        val cacheNewsPage = cache.getString("${faculty.name} $pageNumber", "")
            ?.let { JSONObject(it) }
        if (timestampDifference(timestampNow(), cacheNewsPage!!.getString("created")) < timeCache) {
            async {
                Thread.sleep(100)
                newsResponse.value = cacheNewsPage.getJSONObject("value")
                countPages.intValue = cacheNewsPage.getJSONObject("value").getInt("countPages")
                success.value = true
                newsLoaded.value = true
            }
            return
        }
    }
    newsLoaded.value = false
    var url = "https://$apiDomain/api/news"
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
            cache.edit().putString(
                "${faculty.name} $pageNumber",
                JSONObject().apply {
                    put("created", timestampNow())
                    put("value", newsResponse.value)
                }.toString()
            ).apply()
        },
        {
            success.value = false
            newsLoaded.value = true
            handleVolleyError(it, responseText)
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
    val url = "https://$apiDomain/api/news/${faculty.name}/$id"
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

fun urlForCurrentFaculty(): String {
    return when (selectedFaculty.value.name) {
        "agpu" -> "agpu.net/news.php?PAGEN_1=${currentPage.value}"
        "educationaltechnopark" -> "www.agpu.net/struktura-vuza/educationaltechnopark/news/news.php?PAGEN_1=${currentPage.value}"
        "PedagogicalQuantorium" -> "www.agpu.net/struktura-vuza/PedagogicalQuantorium/news/news.php?PAGEN_1=${currentPage.value}"
        else -> "agpu.net/struktura-vuza/faculties-institutes/${selectedFaculty.value.name}/news/news.php?PAGEN_1=${currentPage.value}"
    }
}