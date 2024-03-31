package com.merqury.aspu.ui.navfragments.messenger

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.mailbox.inbox.InboxType
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.vw
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessengerScreen(header: MutableState<@Composable () -> Unit>) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    var inboxType by remember {
        mutableStateOf(InboxType.Inbox)
    }
    header.value = {
        TitleHeader(
            title = when (inboxType) {
                InboxType.Inbox -> "Входящие"
                InboxType.Outbox -> "Отправленные"
                InboxType.Favorite -> "Избранные"
                InboxType.Deleted -> "Удаленные"
            }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Image(
                Icons.Outlined.Menu, contentDescription = null, modifier = Modifier
                    .clickable {
                        scope.launch {
                            if (swipeableState.currentValue == 0)
                                swipeableState.animateTo(-1, tween(200))
                            else
                                swipeableState.animateTo(0, tween(200))
                        }
                    }
                    .fillMaxHeight(),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
            )
        }
    }

    val density = 20
    val offset = 70f
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTheme.background.color)
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    offset * density to -1,
                    0f to 0
                ),
                orientation = Orientation.Horizontal
            )
    ) {
        Box(
            modifier = Modifier
                .offset(((-offset * 2) + swipeableState.offset.value.toInt() / density).toInt().vw)
                .fillMaxWidth(offset / 100)
                .fillMaxHeight()
                .background(SurfaceTheme.foreground.color)
        )
        Box(
            modifier = Modifier
                .offset(((-offset) + swipeableState.offset.value.toInt() / density).toInt().vw)
                .fillMaxWidth(offset / 100)
                .fillMaxHeight()
                .background(SurfaceTheme.foreground.color)
        ) {
            @Composable
            fun SidebarComponent(
                text: String,
                imageVector: ImageVector,
                type: InboxType
            ) {
                Column(
                    Modifier.clickable {
                        scope.launch {
                            swipeableState.animateTo(0, tween(200))
                        }
                        inboxType = type
                        messagesLoaded = false
                    }
                ) {
                    Spacer(modifier = Modifier.size(15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                imageVector,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Text(text = text, color = SurfaceTheme.text.color, fontSize = 21.sp)
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    Divider(color = SurfaceTheme.divider.color)
                }
            }
            Column {
                SidebarComponent(text = "Входящие", Icons.Outlined.Email, InboxType.Inbox)
                SidebarComponent(text = "Отправленные", Icons.Outlined.Send, InboxType.Outbox)
                SidebarComponent(text = "Избранное", Icons.Outlined.Star, InboxType.Favorite)
                SidebarComponent(text = "Корзина", Icons.Outlined.Delete, InboxType.Deleted)
            }
        }

        //main content
        Box(modifier = Modifier.offset((swipeableState.offset.value.toInt() / density).vw)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (swipeableState.currentValue == -1) {
                    scope.launch {
                        swipeableState.animateTo(0, tween(200))
                    }
                }
            }) {
            Inbox(inboxType, swipeableState.currentValue == 0)
        }
    }
}