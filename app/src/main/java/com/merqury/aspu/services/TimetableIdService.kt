package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.navfragments.timetable.DTO.FacultiesList
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchContent

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
            searchResults.value = SearchContent.fromJson(it)
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
    result: MutableState<FacultiesList>,
    success: MutableState<Boolean>
){
    val url = "https://agpu.merqury.fun/api/timetable/groups"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            result.value = FacultiesList.fromJson(EncodingConverter.translateISO8859_1toUTF_8(it))
            success.value = true
        },
        {
            success.value = false
            Log.d("network-error", "ERROR")
        }
    )
    requestQueue!!.add(request)
}