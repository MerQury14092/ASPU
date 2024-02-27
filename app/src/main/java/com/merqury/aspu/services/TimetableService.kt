package com.merqury.aspu.services

import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import org.json.JSONObject

fun getTimetableByDate(
    id: String,
    owner: String,
    date: String,
    result: MutableState<JSONObject>,
    isLoaded: MutableState<Boolean>
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
        },
        {}
    )
    requestQueue!!.add(request)
}