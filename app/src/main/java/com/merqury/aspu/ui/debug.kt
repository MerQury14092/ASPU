package com.merqury.aspu.ui

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun printlog(everything: Any?){
    Log.d("debug-print", everything.toString())
}
fun tprintlog(everything: Any?){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")
    printlog("${LocalDateTime.now().format(formatter)} -> ${everything.toString()}")
}