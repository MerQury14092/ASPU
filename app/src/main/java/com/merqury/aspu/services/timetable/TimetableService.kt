package com.merqury.aspu.services.timetable

import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.apiDomain
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.misc.cache
import com.merqury.aspu.services.misc.getEndDayOfWeekByDate
import com.merqury.aspu.services.misc.getStartDayOfWeekByDate
import com.merqury.aspu.services.misc.timestampDifference
import com.merqury.aspu.services.misc.timestampNow
import com.merqury.aspu.services.network.EncodingConverter
import com.merqury.aspu.services.network.handleVolleyError
import com.merqury.aspu.services.timetable.models.TimetableDay
import com.merqury.aspu.services.timetable.models.TimetableDay.Companion.toJson
import com.merqury.aspu.ui.async
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getTimetableByDateRange(
    startDate: String,
    endDate: String,
    timetableId: String,
    timetableIdOwner: String,
    onLoad: (result: List<TimetableDay>) -> Unit,
    onError: (e: VolleyError) -> Unit
) {
    val url = "https://$apiDomain/api/v2/timetable/days?" +
            "id=$timetableId" +
            "&owner=$timetableIdOwner" +
            "&startDate=$startDate" +
            "&endDate=$endDate"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            async {
                val response = JSONArray(EncodingConverter.translateISO8859_1toUTF_8(it))
                val timetableDays = ArrayList<TimetableDay>()
                for (i in 0..<response.length()) {
                    timetableDays.add(TimetableDay.fromJson(response.getJSONObject(i).toString()))
                }
                onLoad(timetableDays)
            }
        },
        onError
    )
    requestQueue!!.add(request)

}

fun getTimetableByDate(
    date: String,
    timetableId: String,
    timetableIdOwner: String,
    onError: (String) -> Unit,
    onSuccess: (TimetableDay) -> Unit
) {
    async {
        val timeCache = AppSettings.timeCache
        if (timeCache != 0L && cache.getString("$timetableId $date", "") != "") {
            val cacheTimetableDay = cache.getString("$timetableId $date", "")
                ?.let { JSONObject(it) }
            if (timestampDifference(
                    timestampNow(),
                    cacheTimetableDay!!.getString("created")
                ) < timeCache
            ) {
//                printlog(
//                    "Берем из кэша (debug: {timeCache: $timeCache, timestampDifference: ${
//                        timestampDifference(
//                            timestampNow(), cacheTimetableDay.getString("created")
//                        )
//                    }})"
//                )
                async {
                    Thread.sleep(100)
                    onSuccess(TimetableDay.fromJson(cacheTimetableDay.getString("value")))
                }
                return@async
            }
//            printlog("Кэш просрочился")
        }
//        printlog("Берем не из кэша")

        val startWeekDate = getStartDayOfWeekByDate(date)
        val endWeekDate = getEndDayOfWeekByDate(date)
        getTimetableByDateRange(
            startWeekDate,
            endWeekDate,
            timetableId,
            timetableIdOwner,
            { ttList ->
                ttList.forEach {
                    if (it.date == date) {
                        onSuccess(it)
                    }
                    cache.edit().putString(
                        "$timetableId ${it.date}",
                        JSONObject().apply {
                            put("created", timestampNow())
                            put("value", it.toJson())
                        }.toString()
                    ).apply()
                }
            },
            {
                Log.d("network-error", "ERROR")
                handleVolleyError(it) {
                    onError(it)
                }
            }
        )

    }
}

//@OptIn(DelicateCoroutinesApi::class)
//fun showTimetableWebPageView(date: String) {
//    getSearchId(selectedId.value) { id, type ->
//        GlobalScope.launch {
//            showTimetableWebPageView(id, type, date)
//        }
//    }
//}

//fun showTimetableWebPageView(searchId: Long, searchType: String, date: String) {
//    val url = "www.it-institut.ru/Raspisanie/SearchedRaspisanie?OwnerId=118&SearchId=" +
//            searchId +
//            "&Type=$searchType&WeekId=${WeekIdService.weekIdByDate(date)}" +
//            "&SearchString=${selectedId.value}"
//    val inBrowser = AppSettings.useIncludedBrowser
//    if (inBrowser)
//        showWebPage(url, "https")
//    else
//        openInBrowser(url, "https")
//}

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.now().format(formatter)
}

