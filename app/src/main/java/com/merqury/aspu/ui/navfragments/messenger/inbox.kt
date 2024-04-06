package com.merqury.aspu.ui.navfragments.messenger

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.appContext
import com.merqury.aspu.services.mailbox.inbox.InboxType
import com.merqury.aspu.services.mailbox.inbox.getInbox
import com.merqury.aspu.services.mailbox.inbox.models.MessageElement
import com.merqury.aspu.ui.conditional
import com.merqury.aspu.ui.navfragments.messenger.message.MessageDetailsScreen
import com.merqury.aspu.ui.navfragments.timetable.prettyDate
import com.merqury.aspu.ui.placeholder
import com.merqury.aspu.ui.startTopBarActivityWithActivityLink
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.vw
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

var messagesLoaded by mutableStateOf(false)
val messages = mutableStateListOf<MessageElement>()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Inbox(
    type: InboxType,
    clickable: Boolean
) {


    if (!messagesLoaded) {
        getInbox(inboxType = type) {
            messages.clear()
            messages.addAll(it)
            messagesLoaded = true
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = {
            messagesLoaded = false
        })
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(SurfaceTheme.button.color, RoundedCornerShape(20.dp))
                    .clickable {
                        appContext!!.startTopBarActivityWithActivityLink() { header, activity ->
                            SendMessageScreen(header = header, onBack = {
                                activity!!.finish()
                            })
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = Icons.Outlined.Send, contentDescription = null,
                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                )
            }
        },
        backgroundColor = SurfaceTheme.background.color
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .pullRefresh(pullRefreshState, enabled = messagesLoaded)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            if (messagesLoaded)
                Column {
                    messages.forEach { messageElement ->
                        MessageElement(message = messageElement, type, clickable)
                    }
                }
            else
                Column {
                    (1..5).forEach { _ ->
                        MessageElementLoadingPlaceholder()
                    }
                }
            PullRefreshIndicator(
                refreshing = false,
                state = pullRefreshState,
                contentColor = SurfaceTheme.text.color,
                backgroundColor = SurfaceTheme.foreground.color
            )
        }
    }
}

@Composable
fun MessageElement(message: MessageElement, type: InboxType, clickable: Boolean) {
    Box(
        Modifier
            .padding(5.dp)
            .conditional(clickable) {
                clickable {
                    appContext.startTopBarActivityWithActivityLink { it, activity ->
                        MessageDetailsScreen(
                            header = it,
                            message = message,
                            inTrash = type == InboxType.Deleted
                        ) {
                            activity!!.finish()
                        }
                    }
                }
            }) {
        Box(
            Modifier
                .background(SurfaceTheme.foreground.color, RoundedCornerShape(10.dp))
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column {
                val dateTime = LocalDateTime.parse(
                    message.message!!.dispatchDate!!,
                    DateTimeFormatter.ISO_DATE_TIME
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = message.message.theme!!,
                        color = if (message.dateRead != null && type == InboxType.Inbox) SurfaceTheme.disable.color else SurfaceTheme.text.color,
                        fontWeight = if (message.dateRead != null && type == InboxType.Inbox) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message.userIDFromMessage!!,
                        modifier = Modifier.width(50.vw),
                        color = if (message.dateRead != null && type == InboxType.Inbox) SurfaceTheme.disable.color else SurfaceTheme.text.color,
                    )
                    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    Text(
                        text = "${prettyDate(dateTime.format(dateFormatter))} ${
                            dateTime.format(
                                timeFormatter
                            )
                        }",
                        color = if (message.dateRead != null && type == InboxType.Inbox) SurfaceTheme.disable.color else SurfaceTheme.text.color,
                    )
                }
            }
        }
    }
}

@Composable
fun MessageElementLoadingPlaceholder() {
    Box(Modifier.padding(5.dp)) {
        Box(
            Modifier
                .background(SurfaceTheme.foreground.color, RoundedCornerShape(10.dp))
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Тема этого сообщения интересна",
                        color = SurfaceTheme.text.color,
                        fontSize = 20.sp,
                        modifier = Modifier.placeholder(true)
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Петраков Анатолий Алексеевич",
                        color = SurfaceTheme.text.color,
                        modifier = Modifier.placeholder(true)
                    )
                    Text(
                        text = "25 марта 20254",
                        color = SurfaceTheme.text.color,
                        modifier = Modifier.placeholder(true)
                    )
                }
            }
        }
    }
}