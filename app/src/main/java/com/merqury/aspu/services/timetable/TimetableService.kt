package com.merqury.aspu.services.timetable

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.cache
import com.merqury.aspu.services.getEndDayOfWeekByDate
import com.merqury.aspu.services.getStartDayOfWeekByDate
import com.merqury.aspu.services.network.EncodingConverter
import com.merqury.aspu.services.network.handleVolleyError
import com.merqury.aspu.services.timestampDifference
import com.merqury.aspu.services.timestampNow
import com.merqury.aspu.services.timetable.models.TimetableDay
import com.merqury.aspu.services.timetable.models.TimetableDay.Companion.toJson
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.selectedDate
import com.merqury.aspu.ui.navfragments.timetable.selectedId
import com.merqury.aspu.ui.openInBrowser
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.showWebPage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

fun getTimetableByDateRange(
    id: String,
    owner: String,
    startDate: String,
    endDate: String,
    onLoad: (result: List<TimetableDay>) -> Unit,
    onError: (e: VolleyError) -> Unit
) {
    val url = "https://agpu.merqury.fun/api/v2/timetable/days?" +
            "id=$id" +
            "&owner=$owner" +
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
    id: String,
    owner: String,
    date: String,
    result: MutableState<TimetableDay>,
    isLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>,
    responseText: MutableState<String>
) {
    val timeCache = settingsPreferences.getLong("timeCache", TimeUnit.HOURS.toSeconds(3))
    if (timeCache != 0L && cache.getString("$id $date", "") != "") {
        val cacheTimetableDay = cache.getString("$id $date", "")
            ?.let { JSONObject(it) }
        if (timestampDifference(
                timestampNow(),
                cacheTimetableDay!!.getString("created")
            ) < timeCache
        ) {
            printlog("Берем из кэша (debug: {timeCache: $timeCache, timestampDifference: ${timestampDifference(
                timestampNow(), cacheTimetableDay.getString("created"))}})")
            async {
                Thread.sleep(100)
                result.value = TimetableDay.fromJson(cacheTimetableDay.getString("value"))
                success.value = true
                isLoaded.value = true
            }
            return
        }
        printlog("Кэш просрочился")
    }
    printlog("Берем не из кэша")

    val startWeekDate = getStartDayOfWeekByDate(selectedDate.value)
    val endWeekDate = getEndDayOfWeekByDate(selectedDate.value)
    getTimetableByDateRange(
        id,
        owner,
        startWeekDate,
        endWeekDate,
        { ttList ->
            ttList.forEach {
                if (it.date == selectedDate.value) {
                    result.value = it
                    isLoaded.value = true
                    success.value = true
                }
                cache.edit().putString(
                    "$id ${it.date}",
                    JSONObject().apply {
                        put("created", timestampNow())
                        put("value", it.toJson())
                    }.toString()
                ).apply()
            }
        },
        {
            success.value = false
            isLoaded.value = true
            Log.d("network-error", "ERROR")
            handleVolleyError(it, responseText)
        }
    )

}

@OptIn(DelicateCoroutinesApi::class)
fun showTimetableWebPageView() {
    getSearchId(selectedId.value) { id, type ->
        GlobalScope.launch {
            showTimetableWebPageView(id, type)
        }
    }
}

fun showTimetableWebPageView(searchId: Long, searchType: String) {
    val url = "www.it-institut.ru/Raspisanie/SearchedRaspisanie?OwnerId=118&SearchId=" +
            searchId +
            "&Type=$searchType&WeekId=${WeekIdService.weekIdByDate(selectedDate.value)}" +
            "&SearchString=${selectedId.value}"
    val inBrowser = settingsPreferences.getBoolean("use_included_browser", true)
    if (inBrowser)
        showWebPage(url, "https")
    else
        openInBrowser(url, "https")
}

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.now().format(formatter)
}

