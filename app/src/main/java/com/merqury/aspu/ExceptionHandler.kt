package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.ui.printlog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random
import kotlin.system.exitProcess

class ExceptionHandler(appContext: Context) : Thread.UncaughtExceptionHandler {
    private val mapper = ObjectMapper()
    private val preferences = appContext.getSharedPreferences("exceptions", Context.MODE_PRIVATE)
    private val version by lazy {
        appContext.packageManager
            .getPackageInfo(appContext.packageName, 0).versionName
    }

    @SuppressLint("ApplySharedPref")
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val id = Random.nextLong().toString()
        preferences.edit()
            .putString(id, mapper.writeValueAsString(exception)).commit()
        printlog("caught exception: ${preferences.getString(id, "")}")
        exitProcess(1)
    }

    private fun getAllExceptions(): Map<Long, Throwable> {
        return preferences.all.entries.associate {
            it.key.toLong() to mapper.readValue<Throwable>(it.value as String)
        }
    }

    fun pushAllExceptions() {
        AppConfig.getCrashApiUrl()?.let { url ->
            val client = OkHttpClient()
            getAllExceptions().forEach {
                client.newCall(
                    Request.Builder()
                        .url(url)
                        .post(
                            "\"{version\": \"$version\", \"throwable\":\"${
                                mapper.writeValueAsString(
                                    it.value
                                )
                            }\"}".toRequestBody(
                                "application/json; charset=utf-8".toMediaType()
                            )
                        )
                        .build()
                ).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    override fun onResponse(call: Call, response: Response) {
                        if (response.body?.string() == "200")
                            preferences.edit().remove(it.key.toString()).apply()
                    }
                })
            }
        }

    }
}

