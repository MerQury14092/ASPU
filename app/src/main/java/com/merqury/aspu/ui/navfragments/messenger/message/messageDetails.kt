package com.merqury.aspu.ui.navfragments.messenger.message

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.services.intents.FileOpener
import com.merqury.aspu.services.mailbox.inbox.deleteMessage
import com.merqury.aspu.services.mailbox.inbox.models.MessageElement
import com.merqury.aspu.services.mailbox.inbox.trashMessage
import com.merqury.aspu.services.mailbox.inbox.updateMessage
import com.merqury.aspu.ui.HtmlText
import com.merqury.aspu.ui.MarkdownText
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.navfragments.messenger.messages
import com.merqury.aspu.ui.other.ImageVectorButton
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessageDetailsScreen(
    header: MutableState<@Composable () -> Unit>,
    message: MessageElement,
    inTrash: Boolean,
    finishActivity: () -> Unit
) {
    if (message.dateRead == null) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        message.dateRead = timestamp
        updateMessage(message)
        val changedMessages =
            messages.onEach {
                if (it == message) {
                    it.dateRead = timestamp
                }
            }
                .toList()
        messages.clear()
        messages.addAll(changedMessages)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTheme.background.color)
    ) {
        header.value = {
            var isStarMessage by remember {
                mutableStateOf(message.starMessage == true)
            }
            TitleHeader(title = message.message!!.type!!.type!!)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ImageVectorButton(Icons.Outlined.ArrowBack) {
                    finishActivity()
                }
                Row {
                    ImageVectorButton(
                        Icons.Outlined.Star, color =
                        when (isStarMessage) {
                            true -> Color.Yellow
                            else -> SurfaceTheme.text.color
                        }
                    ) {
                        isStarMessage = !isStarMessage
                        updateMessage(message.apply {
                            starMessage = isStarMessage
                        })
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    ImageVectorButton(
                        Icons.Outlined.Delete, color = if (!inTrash)
                            SurfaceTheme.text.color
                        else Color.Red
                    ) {
                        messages.removeIf {
                            it == message
                        }
                        finishActivity()
                        if (!inTrash) {
                            trashMessage(message)
                            appContext!!.makeToast("Перемещено в корзину!")
                        } else {
                            deleteMessage(message)
                            appContext!!.makeToast("Удалено навсегда!")
                        }

                    }
                }
            }
        }

        Column {
            Box(modifier = Modifier.padding(10.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            SurfaceTheme.foreground.color,
                            RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.man),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .clip(
                                    CircleShape
                                )
                                .size(40.dp)
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Text(
                            text = message.userIDFromMessage!!,
                            color = SurfaceTheme.text.color,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            var detailsInfoShow by remember {
                mutableStateOf(false)
            }
            Row(Modifier.padding(start = 60.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                val dateTime = LocalDateTime.parse(
                    message.message!!.dispatchDate,
                    DateTimeFormatter.ISO_DATE_TIME
                )
                val localDateTimeFormatter = DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, HH:mm"
                )
                Text(
                    text = dateTime.format(localDateTimeFormatter),
                    color = SurfaceTheme.text.color
                )
                Spacer(modifier = Modifier.size(20.dp))
                Image(imageVector =
                if (detailsInfoShow)
                    Icons.Outlined.KeyboardArrowUp
                else
                    Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                    modifier = Modifier.clickable {
                        detailsInfoShow = !detailsInfoShow
                    }
                )
            }
            if (detailsInfoShow) {
                Column(
                    Modifier.padding(start = 20.dp)
                ) {
                    Text(text = "Кому: ${message.userIDToMessage}", color = SurfaceTheme.text.color)
                    Text(
                        text = "Тип сообщения: ${message.message!!.type!!.type}",
                        color = SurfaceTheme.text.color
                    )
                }
            }

            Column(
                Modifier.padding(start = 15.dp, top = 20.dp)
            ) {
                Row {
                    Text(
                        text = "Тема: ",
                        color = SurfaceTheme.text.color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${message.message!!.theme}", color = SurfaceTheme.text.color)
                }
                Spacer(modifier = Modifier.size(20.dp))
                Row {
                    Text(
                        text = "Текст: ",
                        color = SurfaceTheme.text.color,
                        fontWeight = FontWeight.Bold
                    )
                    if (message.message!!.htmlMessage != null)
                        HtmlText(
                            text = message.message.htmlMessage!!,
                            color = SurfaceTheme.text.color
                        )
                    else
                        MarkdownText(
                            text = message.message.markdownMessage!!,
                            color = SurfaceTheme.text.color
                        )
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Divider(color = SurfaceTheme.divider.color)
            Column(Modifier.padding(20.dp)) {
                message.files?.onEach {
                    Box(
                        modifier = Modifier
                            .background(
                                SurfaceTheme.foreground.color,
                                RoundedCornerShape(5.dp)
                            )
                            .padding(10.dp)
                            .clickable {
                                FileOpener.open(
                                    appContext!!,
                                    "http://plany.agpu.net${it.path}"
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = it.fileName!!, color = SurfaceTheme.text.color)
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}