package com.merqury.aspu.services.profile

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.profile.models.MarkStat
import com.merqury.aspu.ui.after
import com.merqury.aspu.ui.navfragments.profile.secretPreferences
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

fun getMarkStatsById(
    id: Long,
    onError: () -> Unit = {},
    onResult: (MarkStat) -> Unit
){
    val url = "http://plany.agpu.net/api/EducationalActivity/StatisticsMarksCount?studentID=$id"
    val request = object: StringRequest(
        Request.Method.GET,
        url,
        {
            onResult(MarkStat.fromJson(it))
        },
        {
            onError()
        }
    ){
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer ${secretPreferences.getString("authToken", null)!!}"
            return headers
        }
    }
    after(3.seconds){
        requestQueue!!.add(request)
    }
}

fun getAvg(marks: List<MarkStat.MarkCountStatistic>): String{
    var res = 0L
    var count = 0L
    marks.forEach {
        count += it.count!!
        res += it.mark!!*it.count
    }

    val roundedRes = ((res.toDouble()/count)*100).roundToLong().toDouble()/100
    val ost = roundedRes-roundedRes.roundToInt()
    return if (ost == 0.0)
        roundedRes.roundToLong().toString()
    else
        roundedRes.toString()
}