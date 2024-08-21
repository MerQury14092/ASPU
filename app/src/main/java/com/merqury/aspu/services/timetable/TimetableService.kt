package com.merqury.aspu.services.timetable

import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.apiDomain
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.api.timetable.GetTimetableService
import com.merqury.aspu.services.api.timetable.enums.TimetableOwner
import com.merqury.aspu.services.network.EncodingConverter
import com.merqury.aspu.services.timetable.models.Discipline
import com.merqury.aspu.services.timetable.models.TimetableDay
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.printlog
import org.json.JSONArray
import java.net.UnknownHostException
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
    val service = GetTimetableService.getInstance()
    async {
        try {
            val response = service.getTimetableWeek(
                timetableId,
                date,
                TimetableOwner.valueOf(timetableIdOwner.uppercase())
            )
            if (response.any { it.date == date }) {
                val day = response.first { it.date == date }
                onSuccess(
                    TimetableDay(
                        day.date,
                        day.id,
                        day.owner.name,
                        day.disciplines.map {
                            Discipline(
                                it.time,
                                it.name,
                                it.teacherName,
                                it.audienceId,
                                it.subgroup,
                                it.type.name,
                                it.groupName,
                                it.isDistant
                            )
                        }.toList()
                    )
                )
            } else
            onSuccess (
                TimetableDay(
                    date, timetableId, timetableIdOwner, listOf()
                )
            )
        } catch (e: Exception) {
            if(e.cause is UnknownHostException) {
                onError("Нет подключения к сети Интернет")
            } else {
                printlog("TIMETABLE FETCH ERROR: $e")
                onError("Неизвестная ошибка")
            }}

    }
}

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return LocalDate.now().format(formatter)
}

