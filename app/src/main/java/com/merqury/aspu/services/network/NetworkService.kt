package com.merqury.aspu.services.network

import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError

fun handleVolleyError(it: VolleyError, onResult: (String) -> Unit){
    if(it.javaClass == NoConnectionError::class.java) {
        onResult("Нет подключения к интернету!")
    }
    else if(it.javaClass == ServerError::class.java)
        if (it.networkResponse.statusCode == 502)
            onResult("На сервере ведутся плановые технические работы.")
        else if(it.networkResponse.statusCode >= 500)
            onResult("Ошибка на стороне сервера")
        else
            onResult("Неизвестная ошибка! Отчёт был анонимно отправлен разработчику.")
    else if (it.javaClass == TimeoutError::class.java)
        onResult("Истекло время ожидания ответа. Возможно у вас проблемы с интернетом")
}