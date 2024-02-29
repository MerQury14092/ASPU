package com.merqury.aspu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.merqury.aspu.show

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
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismiss()
        }) {
        Card(modifier = modifier) {
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
){
    showSimpleUpdatableModalWindow (
        modifier = modifier,
        onClosed = onClosed,
        closeable = closeable,
        containerColor = containerColor
    ){ showed, _, _ ->
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
    show {
        val forUpdate = remember {
            mutableStateOf(false)
        }
        val update = {
            forUpdate.value = !forUpdate.value
        }
        val showed = remember {
            mutableStateOf(true)
        }
        if (showed.value)
            Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    if(closeable)
                        showed.value = false
                    onClosed()
                }) {
                Card(modifier = modifier, colors = CardDefaults.cardColors(
                    containerColor = containerColor
                )) {
                    forUpdate.value
                    content(showed, update, forUpdate)
                }
            }
    }
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
                modifier = Modifier.offset(x = (summaryOffset.floatValue / 4).dp)
            ) {
                content()
            }
        }
    }

}

fun showSelectListDialog(
    buttons: Map<String, () -> Unit>
) {
    showSimpleModalWindow {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(.5f)
        ) {
            val modalWindowVisibility = it
            Column {
                buttons.entries.forEach {
                    Divider()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clickable {
                                it.value()
                                modalWindowVisibility.value = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = it.key, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

fun showWebPage(url: String){
    showSimpleModalWindow {
        WebView(rememberWebViewState(url))
    }
}