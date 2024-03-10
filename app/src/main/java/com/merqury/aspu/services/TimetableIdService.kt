package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchContent
import org.json.JSONArray

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
                searchResults.value = SearchContent.fromJson(it)
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

fun getFacultiesAndThemGroups(
    result: MutableState<JSONArray>,
    loaded: MutableState<Boolean>,
    success: MutableState<Boolean>
){
    val url = "https://agpu.merqury.fun/api/timetable/groups"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            result.value = JSONArray(EncodingConverter.translateISO8859_1toUTF_8(it))
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