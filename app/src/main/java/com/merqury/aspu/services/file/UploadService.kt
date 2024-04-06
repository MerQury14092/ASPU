package com.merqury.aspu.services.file

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.file.models.FileModel
import com.merqury.aspu.services.network.EncodingConverter
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.printlog
import org.json.JSONArray
import java.io.IOException
import java.net.URI

fun allocate(onAllocated: (Int) -> Unit) {
    async {
        try {
            onAllocated(
                URI.create("https://eios.site/upload").toURL().readText().toInt()
            )
        } catch (e: IOException) {

        }
    }
}

fun longPollingGetFiles(id: Int, onStartLoading: () -> Unit = {}, onResult: (List<FileModel>) -> Unit) {
    val url = "https://eios.site/$id/get"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {resp ->
            onStartLoading()
            val it = EncodingConverter.translateISO8859_1toUTF_8(resp)
            if (it == "")
                longPollingGetFiles(id, onStartLoading, onResult)
            else {
                val array = JSONArray(it)
                val res = ArrayList<FileModel>()
                (0..<array.length()).forEach { i ->
                    res.add(FileModel.fromJson(array.getJSONObject(i).toString()))
                }
                onResult(res)
            }
        },
        {
            if(it.javaClass == TimeoutError::class.java)
                longPollingGetFiles(id, onStartLoading, onResult)
            else if ((it.networkResponse?.statusCode ?: 0) == 400) {
                longPollingGetFiles(id, onStartLoading, onResult)
            } else
                printlog(it)
        }
    )
    request.setTag("long polling get files")
    requestQueue!!.add(request)
}

fun downloadFile(id: Int, file: FileModel, closure: () -> Unit = {}) {
    val url = "https://eios.site/$id/get/${file.fileName}"
    val request = object: Request<ByteArray>(
        Method.GET,
        url,
        {}
    ){
        override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray> {
            return if(response?.statusCode == 200)
                Response.success(response.data, null)
            else
                Response.error(VolleyError())
        }

        override fun deliverResponse(response: ByteArray?) {
            file.content = response
            closure()
        }
    }
    requestQueue!!.add(request)
}