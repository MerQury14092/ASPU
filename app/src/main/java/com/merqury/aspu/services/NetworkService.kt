package com.merqury.aspu.services

import androidx.compose.runtime.MutableState
import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError

fun handleVolleyError(it: VolleyError, responseText: MutableState<String>){
    if(it.javaClass == NoConnectionError::class.java) {
        if(it.message!!.contains("UnknownHost"))
            responseText.value = "Ошибка подключения к API: хост изменился"
        else
            responseText.value = "Нет подключения к интернету!"
    }
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