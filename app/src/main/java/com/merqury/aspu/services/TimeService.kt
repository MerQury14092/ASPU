package com.merqury.aspu.services

import android.content.Context
import android.content.SharedPreferences
import com.merqury.aspu.appContext
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

fun timestampNow(): String{
    return LocalDateTime.now().format(formatter)
}