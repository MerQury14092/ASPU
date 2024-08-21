package com.merqury.aspu.ui

import android.util.Log
import androidx.compose.runtime.Composable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Debug {
    companion object {
        fun printlog(everything: Any?) {
            com.merqury.aspu.ui.printlog(everything)
        }
    }
}

fun printlog(everything: Any?){
    Log.d("debug-print", everything.toString())
}
fun tprintlog(everything: Any?){
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")
    printlog("${LocalDateTime.now().format(formatter)} -> ${everything.toString()}")
}

@Composable
fun measureDrawTime(label: String, code: @Composable () -> Unit){
    val before = System.nanoTime()
    code()
    val after = System.nanoTime()
    printlog("$label: ${(after-before)/1_000_000} ms")
}

fun measureTime(label: String, code: () -> Unit){
    val before = System.nanoTime()
    code()
    val after = System.nanoTime()
    printlog("$label: ${(after-before)/1_000_000} ms")
}