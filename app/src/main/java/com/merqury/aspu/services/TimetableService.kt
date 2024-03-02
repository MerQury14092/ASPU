package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchContent
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getTimetableByDate(
    id: String,
    owner: String,
    date: String,
    result: MutableState<JSONObject>,
    isLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>
) {
    isLoaded.value = false
    val url = "https://agpu.merqury.fun/api/timetable/day?id=$id&owner=$owner&date=$date"
    val request = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val convertedResponse = EncodingConverter.translateISO8859_1toUTF_8(response)
            result.value = JSONObject(convertedResponse)
            isLoaded.value = true
            success.value = true
        },
        {
            success.value = false
            isLoaded.value = true
            Log.d("network-error", "ERROR")
        }
    )
    requestQueue!!.add(request)
}

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.now().format(formatter)
}

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