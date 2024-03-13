package com.merqury.aspu.services

import android.content.Context
import android.content.SharedPreferences
import com.merqury.aspu.appContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd:HH.mm.ss")
val cache: SharedPreferences = appContext!!.getSharedPreferences("news-cache", Context.MODE_PRIVATE)
fun timestampDifference(a: String, b: String): Long{
    val dtA = LocalDateTime.parse(a, formatter)
    val dtB = LocalDateTime.parse(b, formatter)
    return ChronoUnit.SECONDS.between(dtA, dtB)
}

fun forEachDayInWeekByDate(date: String, runnable: (day: String) -> Unit){
    val localFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var nowDate = LocalDate.parse(date, localFormatter)
    while (nowDate.dayOfWeek.value != 1)
        nowDate = nowDate.minusDays(1)
    while (nowDate.dayOfWeek.value < 7){
        runnable(nowDate.format(localFormatter))
        nowDate = nowDate.plusDays(1)
    }
    runnable(nowDate.format(localFormatter))
}

fun timestampNow(): String{
    return LocalDateTime.now().format(formatter)
}