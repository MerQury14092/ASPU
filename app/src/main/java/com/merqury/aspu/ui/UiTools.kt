package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    gifResourceId: Int
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
        modifier = modifier.fillMaxWidth(),
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ModalWindow(modifier: Modifier = Modifier,  onDismiss: () -> Unit = {}, content: @Composable ColumnScope.() -> Unit){
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Card (modifier = modifier) {
            content()
        }
    }
}