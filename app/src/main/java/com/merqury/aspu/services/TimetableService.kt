package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.apiDomain
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.DTO.TimetableDay
import com.merqury.aspu.ui.navfragments.timetable.selectedDate
import com.merqury.aspu.ui.navfragments.timetable.selectedId
import com.merqury.aspu.ui.openInBrowser
import com.merqury.aspu.ui.showWebPage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


fun getTimetableByDate(
    id: String,
    owner: String,
    date: String,
    result: MutableState<TimetableDay>,
    isLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>,
    responseText: MutableState<String>
) {
    oldGetTimetableByDate(id, owner, date, result, isLoaded, success, responseText)
    forEachDayInWeekByDate(selectedDate.value) {
        oldGetTimetableByDate(
            id,
            owner,
            it,
            mutableStateOf(TimetableDay("", "", "", listOf())),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf("")
        )
    }
}

fun oldGetTimetableByDate(
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
            async {
                Thread.sleep(100)
                result.value = TimetableDay.fromJson(cacheTimetableDay.getString("value"))
                success.value = true
                isLoaded.value = true
            }
            return
        }
    }
    isLoaded.value = false
    val url = "https://$apiDomain/api/timetable/day?id=$id&owner=$owner&date=$date"
    val request = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val convertedResponse = EncodingConverter.translateISO8859_1toUTF_8(response)
            result.value = TimetableDay.fromJson(convertedResponse)
            isLoaded.value = true
            success.value = true
            cache.edit().putString(
                "$id $date",
                JSONObject().apply {
                    put("created", timestampNow())
                    put("value", convertedResponse)
                }.toString()
            ).apply()
        },
        {
            success.value = false
            isLoaded.value = true
            Log.d("network-error", "ERROR")
            handleVolleyError(it, responseText)
        }
    )
    requestQueue!!.add(request)
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
    val inBrowser = settingsPreferences.getBoolean("use_included_browser", false)
    if (inBrowser)
        showWebPage(url, "https")
    else
        openInBrowser(url, "https")
}

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.now().format(formatter)
}

