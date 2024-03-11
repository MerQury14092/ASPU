package com.merqury.aspu.services

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.ui.navfragments.timetable.DTO.TimetableDay
import com.merqury.aspu.ui.navfragments.timetable.selectedDate
import com.merqury.aspu.ui.navfragments.timetable.selectedId
import com.merqury.aspu.ui.showWebPage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getTimetableByDate(
    id: String,
    owner: String,
    date: String,
    result: MutableState<TimetableDay>,
    isLoaded: MutableState<Boolean>,
    success: MutableState<Boolean>,
    responseText: MutableState<String>
) {
    isLoaded.value = false
    val url = "https://agpu.merqury.fun/api/timetable/day?id=$id&owner=$owner&date=$date"
    val request = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val convertedResponse = EncodingConverter.translateISO8859_1toUTF_8(response)
            result.value = TimetableDay.fromJson(convertedResponse)
            isLoaded.value = true
            success.value = true
        },
        {
            success.value = false
            isLoaded.value = true
            Log.d("network-error", "ERROR")
            if(it.javaClass == NoConnectionError::class.java)
                responseText.value = "Нет подключения к интернету!"
            else if(it.javaClass == ServerError::class.java)
                if (it.networkResponse.statusCode == 502)
                    responseText.value = "На сервере ведутся плановые технические работы."
                else if(it.networkResponse.statusCode >= 500)
                    responseText.value = "Ошибка на стороне сервера"
                else
                    responseText.value = "Неизвестная ошибка! Отчёт был анонимно отправлен разработчику."
            else if (it.javaClass == TimeoutError::class.java)
                responseText.value = "Истекло время ожидания ответа. Возможно у вас проблемы с интернетом"
        }
    )
    requestQueue!!.add(request)
}

fun showTimetableWebPageView(){
    getSearchId(selectedId.value) {id, type ->
        showTimetableWebPageView(id, type)
    }
}
fun showTimetableWebPageView(searchId: Long, searchType: String){
    val url = "www.it-institut.ru/Raspisanie/SearchedRaspisanie?OwnerId=118&SearchId=" +
            searchId +
            "&Type=$searchType&WeekId=${WeekIdService.weekIdByDate(selectedDate.value)}" +
            "&SearchString=${selectedId.value}"
    showWebPage(url, "https")
}

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.now().format(formatter)
}

