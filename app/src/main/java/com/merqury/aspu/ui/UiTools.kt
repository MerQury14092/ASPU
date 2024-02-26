package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun ModalWindow(modifier: Modifier = Modifier,  onDismiss: () -> Unit = {}, content: @Composable () -> Unit){
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
        onDismiss()
    }) {
        Card (modifier = modifier) {
            content()
        }
    }
}