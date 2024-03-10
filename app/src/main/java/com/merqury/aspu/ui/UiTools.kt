package com.merqury.aspu.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.merqury.aspu.R
import com.merqury.aspu.close
import com.merqury.aspu.context
import com.merqury.aspu.show
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import com.merqury.aspu.ui.theme.themeChangeDuration

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    gifResourceId: Int,
    contentScale: ContentScale = ContentScale.None
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(ImageDecoderDecoder.Factory())
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = gifResourceId).apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun ModalWindow(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    background: Color = Color.White,
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismiss()
        }) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = background)
        ) {
            content()
        }
    }
}

fun showSimpleModalWindow(
    modifier: Modifier = Modifier,
    onClosed: () -> Unit = {},
    closeable: Boolean = true,
    containerColor: Color = Color.White,
    content: @Composable (showed: MutableState<Boolean>) -> Unit
) {
    showSimpleUpdatableModalWindow(
        modifier = modifier,
        onClosed = onClosed,
        closeable = closeable,
        containerColor = containerColor
    ) { showed, _, _ ->
        content(showed)
    }
}

fun showSimpleUpdatableModalWindow(
    modifier: Modifier = Modifier,
    onClosed: () -> Unit = {},
    closeable: Boolean = true,
    containerColor: Color = Color.White,
    content: @Composable (showed: MutableState<Boolean>, update: () -> Unit, forUpdate: MutableState<Boolean>) -> Unit
) {
    var dialogContent: @Composable () -> Unit = {}
    val showed = mutableStateOf(true)
    dialogContent = {
        val forUpdate = remember {
            mutableStateOf(false)
        }
        val update = {
            forUpdate.value = !forUpdate.value
        }
        if (showed.value)
            Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    if (closeable)
                        showed.value = false
                    onClosed()
                    close(dialogContent)
                }) {
                Card(
                    modifier = modifier, colors = CardDefaults.cardColors(
                        containerColor = containerColor
                    )
                ) {
                    forUpdate.value
                    content(showed, update, forUpdate)
                }
            }
        else
            close(dialogContent)
    }
    show(showed, dialogContent)
}

@Composable
fun SwipeableBox(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeableRight: Boolean = true,
    swipeableLeft: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val summaryOffset = remember {
        mutableFloatStateOf(0f)
    }
    val lastOffsets = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (swipeableLeft && (summaryOffset.floatValue >= 500f || lastOffsets.max() >= 60f)) {

                        onSwipeLeft()
                    }
                    if (swipeableRight && (summaryOffset.floatValue <= -500f || lastOffsets.min() <= -60f)) {

                        onSwipeRight()
                    }
                    summaryOffset.floatValue = 0f
                    lastOffsets.indices.forEach {
                        lastOffsets[it] = 0f
                    }
                }
            ) { _, dragAmount ->
                summaryOffset.floatValue += dragAmount
                (lastOffsets.size - 1 downTo 1).forEach {
                    lastOffsets[it] = lastOffsets[it - 1]
                }
                lastOffsets[0] = dragAmount
            }

        }
    ) {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier.offset(
                    x = animateDpAsState(
                        targetValue = (summaryOffset.floatValue / 3).dp,
                        animationSpec = tween(durationMillis =
                            if (summaryOffset.floatValue == 0f) 250 else 50
                        ), label = ""
                    ).value
                )
            ) {
                content()
            }
        }
    }

}

fun async(runnable: () -> Unit){
    Thread {runnable()}.start()
}

fun showSelectListDialog(
    buttons: Map<String, () -> Unit>,
    sortedByAlphabet: Boolean = false
) {
    showSelectListDialog(
        mutableStateOf(buttons),
        sortedByAlphabet
    )
}

fun showSelectListDialog(
    buttons: MutableState<Map<String, () -> Unit>>,
    sortedByAlphabet: Boolean = false
) {
    showSimpleModalWindow(
        containerColor = theme.value[SurfaceTheme.background]!!
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.75f)
                .verticalScroll(rememberScrollState())
        ) {
            val modalWindowVisibility = it
            Column {
                val entries = if (sortedByAlphabet)
                    buttons.value.entries.sortedBy { it.key }
                else
                    buttons.value.entries
                entries.forEach {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = theme.value[SurfaceTheme.foreground]!!
                        ),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable {
                                    it.value()
                                    modalWindowVisibility.value = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it.key,
                                fontSize = 20.sp,
                                color = theme.value[SurfaceTheme.text]!!
                            )
                        }
                    }
                }
            }
        }
    }
}

fun showWebPage(url: String, scheme: String) {
    if (scheme in listOf("http", "https")) {
        showSimpleModalWindow {
            val webViewState = rememberWebViewState("$scheme://$url")
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.06f)
                        .background(theme.value[SurfaceTheme.foreground]!!)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                it.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.value[SurfaceTheme.button]!!
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(theme.value[SurfaceTheme.text]!!),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Button(
                            onClick = {
                                it.value = false
                                val urlParts = webViewState.lastLoadedUrl!!.split("://")
                                openInBrowser(urlParts[1],urlParts[0])
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.value[SurfaceTheme.button]!!
                            )
                        ) {
                            Text(
                                text = "Открыть в браузере",
                                color = theme.value[SurfaceTheme.text]!!
                            )
                            Image(
                                painter = painterResource(id = R.drawable.browser),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(theme.value[SurfaceTheme.text]!!),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Divider(color = theme.value[SurfaceTheme.divider]!!)
                Box(modifier = Modifier.fillMaxSize()) {
                    WebView(webViewState)
                }
            }
        }
    } else
        openInBrowser(url, scheme)
}

fun openInBrowser(url: String, scheme: String) {
    try {
        context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$url")))
    } catch (e: ActivityNotFoundException) {
        context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://$url")))
    }
}

@Composable
fun TitleHeader(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            color = animateColorAsState(
                targetValue = theme.value[SurfaceTheme.text]!!,
                animationSpec = tween(durationMillis = themeChangeDuration),
                label = ""
            ).value,
            fontSize = 24.sp,
            fontStyle = FontStyle.Italic
        )
    }
}
