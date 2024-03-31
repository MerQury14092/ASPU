package com.merqury.aspu.services.mailbox.inbox

import com.android.volley.Request
import com.android.volley.TimeoutError
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.mailbox.inbox.models.Inbox
import com.merqury.aspu.services.mailbox.inbox.models.MessageElement
import com.merqury.aspu.services.profile.AuthorizedStringRequest
import com.merqury.aspu.ui.printlog

fun getInbox(
    inboxType: InboxType = InboxType.Deleted,
    onSuccess: (List<MessageElement>) -> Unit
) {
    requestQueue!!.cancelAll("getinbox")
    val url =
        "http://plany.agpu.net/api/Mail/InboxMail?type=${inboxType.type}&pageEl=10000&unreadMessages=false&searchQuery=&modeParent=0"
    val request = AuthorizedStringRequest(
        Request.Method.GET,
        url,
        {
            val inbox = Inbox.fromJson(it)
            val messages = inbox.data!!.messages!!
            onSuccess(messages)
        },
        {
            if (it is TimeoutError)
                getInbox(inboxType, onSuccess)
            else
                printlog(it)
        }
    )
    request.setTag("getinbox")
    requestQueue!!.add(request)
}

enum class InboxType(val type: Int) {
    Inbox(0),
    Outbox(1),
    Favorite(3),
    Deleted(2)
}