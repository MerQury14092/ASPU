package com.merqury.aspu.services.mailbox.inbox

import com.android.volley.Request
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.mailbox.inbox.models.ActionType
import com.merqury.aspu.services.mailbox.inbox.models.ListMail
import com.merqury.aspu.services.mailbox.inbox.models.MessageElement
import com.merqury.aspu.services.mailbox.inbox.models.MessageState
import com.merqury.aspu.services.mailbox.inbox.models.MessageUpdateAction
import com.merqury.aspu.services.profile.AuthorizedStringRequest

fun updateMessage(message: MessageElement){
    val url = "http://plany.agpu.net/api/Mail/InboxMail/${message.id}"
    val request = object: AuthorizedStringRequest(
        Request.Method.PUT,
        url,
        {

        },
        {
            throw it
        }
    ) {
        override fun getBodyContentType(): String {
            return "application/json"
        }

        override fun getBody(): ByteArray {
            return MessageState(
                message.dateRead,
                message.id,
                message.isDelete,
                message.messageID,
                message.folderID,
                message.recipientID,
                message.starMessage
            ).toJson().toByteArray()
        }
    }
    requestQueue!!.add(request)
}

fun deleteMessage(message: MessageElement){
    doMessageAction(message, ActionType.Delete)
}

fun trashMessage(message: MessageElement){
    updateMessage(message.apply {
        isDelete = 1
    })
}

fun doMessageAction(message: MessageElement, actionType: ActionType){
    val url = "http://plany.agpu.net/api/Mail/typeMail"
    val json = MessageUpdateAction(
        actionTypeID = actionType.id,
        selectedFolderID = message.folderID,
        listOf(
            ListMail(
                message.folderID,
                message.messageID,
                message.message,
                message.isDelete,
                message.recipientID,
                message.message?.userID
            )
        )
    ).toJson()
    val request = object: AuthorizedStringRequest(
        Request.Method.POST,
        url,
        {

        },
        {
            throw it
        }
    ) {
        override fun getBodyContentType(): String {
            return "application/json"
        }

        override fun getBody(): ByteArray {

            return json.toByteArray()
        }
    }
    requestQueue!!.add(request)
}