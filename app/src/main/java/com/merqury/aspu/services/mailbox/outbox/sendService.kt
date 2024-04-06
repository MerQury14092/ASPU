package com.merqury.aspu.services.mailbox.outbox

import com.merqury.aspu.appContext
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.file.models.FileModel
import com.merqury.aspu.services.mailbox.outbox.models.FioSearchElement
import com.merqury.aspu.services.mailbox.outbox.models.SendMessageModel
import com.merqury.aspu.services.mailbox.outbox.models.SendedMessageResponse
import com.merqury.aspu.services.profile.AuthorizedStringRequest
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.printlog
import java.nio.charset.StandardCharsets

fun sendMessage(
    theme: String,
    markdown: String,
    usersToId: List<FioSearchElement>,
    files: List<FileModel> = listOf()
) {
    val url = "http://plany.agpu.net/api/Mail/InboxMail"
    val request = object : AuthorizedStringRequest(
        Method.POST,
        url,
        {
            if (files.isEmpty())
                appContext!!.makeToast("Отправлено")
            else {
                val response = SendedMessageResponse.fromJson(it)
                files.forEach { file ->
                    sendFileToEiosMessage(response.data!!.messageID!!, file)
                }
            }
        },
        {
            appContext!!.makeToast("Не отправлено :(")
            printlog(it)
        }
    ) {
        override fun getBodyContentType(): String {
            return "application/json"
        }

        override fun getBody(): ByteArray {
            val body = SendMessageModel(
                markdownMessage = markdown,
                userToID = usersToId,
                theme = theme
            ).toJson()
            return body.toByteArray(StandardCharsets.UTF_8)
        }
    }
    requestQueue!!.add(request)
}