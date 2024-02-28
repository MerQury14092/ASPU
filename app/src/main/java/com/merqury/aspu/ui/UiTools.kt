package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

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

@SuppressLint("UnrememberedMutableState")
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
        Box(modifier = modifier){
            Box(
                modifier = Modifier.offset(x = (summaryOffset.floatValue / 4).dp)
            ) {
                content()
            }
        }
    }

}