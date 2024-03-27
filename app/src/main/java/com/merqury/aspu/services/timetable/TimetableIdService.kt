package com.merqury.aspu.services.timetable

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.apiDomain
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.network.EncodingConverter
import com.merqury.aspu.ui.async
import com.merqury.aspu.services.timetable.models.FacultiesList
import com.merqury.aspu.services.timetable.models.SearchContent
import java.net.URI
import java.net.URLEncoder

fun getSearchResults(
    query: String,
    searchResults: MutableState<SearchContent>,
    success: MutableState<Boolean>
) {
    val url = "https://www.it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=$query"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            async {
                val res = SearchContent.fromJson(it)
                res.forEach {
                    it.searchContent = it.searchContent.split(",")[0]
                }
                searchResults.value = res
            }
            success.value = true
        },
        {
            success.value = false
            Log.d("network-error", "ERROR")
        }
    )
    requestQueue!!.add(request)
}

fun getSearchId(query: String, onLoaded: (resultId: Long, resultType: String) -> Unit) {
    async {
        val resp = URI(
            "https://www.it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=${
                URLEncoder.encode(
                    query,
                    "utf-8"
                )
            }"
        ).toURL().readText()

        val type = resp
            .split("Type\":\"")[1]
            .split("\"")[0]

        val searchId = resp
            .split("SearchId\":")[1]
            .split(",")[0]
            .toLong()

        onLoaded(searchId, type)
    }
}

fun getFacultiesAndThemGroups(
    result: MutableState<FacultiesList>,
    loaded: MutableState<Boolean>,
    success: MutableState<Boolean>
) {
    val url = "https://$apiDomain/api/timetable/groups"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            result.value = FacultiesList.fromJson(EncodingConverter.translateISO8859_1toUTF_8(it))
            success.value = true
            loaded.value = true
        },
        {
            success.value = false
            loaded.value = true
            Log.d("network-error", "ERROR")
        }
    )
    requestQueue!!.add(request)
}