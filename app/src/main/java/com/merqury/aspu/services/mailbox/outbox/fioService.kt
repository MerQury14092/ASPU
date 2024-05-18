package com.merqury.aspu.services.mailbox.outbox

import com.android.volley.Request
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.mailbox.outbox.models.FioSearchElement
import com.merqury.aspu.services.mailbox.outbox.models.PrepodFioModel
import com.merqury.aspu.services.mailbox.outbox.models.StudentFioModel
import com.merqury.aspu.services.profile.AuthorizedStringRequest

fun getFioStudent(q: String, onSuccess: (List<FioSearchElement>) -> Unit){
    requestQueue!!.cancelAll("fiorequest")
    val url = "http://plany.agpu.net/api/Mail/Find/Students?fio=$q"
    val request = AuthorizedStringRequest(
        Request.Method.GET,
        url,
        {
            val res = StudentFioModel.fromJson(it)
            onSuccess(res.data!!.arrStud!!)
        },
        {
            throw it // TODO: volley error handle
        }
    )
    request.setTag("fiorequest")
    requestQueue!!.add(request)
}

fun getFioPrepod(q: String, onSuccess: (List<FioSearchElement>) -> Unit){
    requestQueue!!.cancelAll("fiorequest")
    val url = "http://plany.agpu.net/api/Mail/Find/Prepods?fio=$q"
    val request = AuthorizedStringRequest(
        Request.Method.GET,
        url,
        {
            val res = PrepodFioModel.fromJson(it)
            onSuccess(res.data!!.arrPrep!!)
        },
        {
            throw it // TODO: volley error handle
        }
    )
    request.setTag("fiorequest")
    requestQueue!!.add(request)
}