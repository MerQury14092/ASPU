package com.merqury.aspu.services.news

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.apiDomain
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.misc.cache
import com.merqury.aspu.services.misc.timestampDifference
import com.merqury.aspu.services.misc.timestampNow
import com.merqury.aspu.services.network.EncodingConverter
import com.merqury.aspu.services.network.handleVolleyError
import com.merqury.aspu.ui.async
import org.json.JSONObject

fun getNews(
    pageNumber: Int,
    selectedFaculty: NewsCategoryEnum,
    onError: (String) -> Unit,
    onSuccess: (JSONObject, Int) -> Unit
) {
    Thread.sleep(100)
    val timeCache = AppSettings.timeCache
    if (timeCache != 0L && cache.getString("${selectedFaculty.name} $pageNumber", "") != "") {
        async {
            val cacheNewsPage = cache.getString("${selectedFaculty.name} $pageNumber", "")
                ?.let { JSONObject(it) }
            if (timestampDifference(timestampNow(), cacheNewsPage!!.getString("created")) < timeCache) {

                Thread.sleep(500)
                onSuccess(
                    cacheNewsPage.getJSONObject("value"),
                    cacheNewsPage.getJSONObject("value").getInt("countPages")
                )
            }
        }
    } else {
        var url = "https://$apiDomain/api/news"
        if (selectedFaculty != NewsCategoryEnum.agpu)
            url = "$url/${selectedFaculty.name}"
        url = "$url?page=$pageNumber"
        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                async {
                    val convertedResponse = EncodingConverter.translateISO8859_1toUTF_8(response)
                    val res = JSONObject(convertedResponse).put("category", selectedFaculty.name)
                    onSuccess(
                        res,
                        res.getInt("countPages")
                    )
                    cache.edit().putString(
                        "${selectedFaculty.name} $pageNumber",
                        JSONObject().apply {
                            put("created", timestampNow())
                            put("value", res)
                        }.toString()
                    ).apply()
                }
            },
            {
                handleVolleyError(it) { errorMessage ->
                    onError(errorMessage)
                }
            }
        )
        requestQueue!!.add(request)
    }
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

//@OptIn(ExperimentalFoundationApi::class)
//fun urlForCurrentFaculty(): String {
//    return when (selectedFaculty.value.name) {
//        "agpu" -> "agpu.net/news.php?PAGEN_1=${pagerState.value.currentPage}"
//        "educationaltechnopark" -> "www.agpu.net/struktura-vuza/educationaltechnopark/news/news.php?PAGEN_1=${pagerState.value.currentPage}"
//        "PedagogicalQuantorium" -> "www.agpu.net/struktura-vuza/PedagogicalQuantorium/news/news.php?PAGEN_1=${pagerState.value.currentPage}"
//        else -> "agpu.net/struktura-vuza/faculties/${selectedFaculty.value.name}/news/news.php?PAGEN_1=${pagerState.value.currentPage}"
//    }
//}