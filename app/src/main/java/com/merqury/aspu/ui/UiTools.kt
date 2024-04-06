package com.merqury.aspu.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.appContext
import com.merqury.aspu.close
import com.merqury.aspu.show
import com.merqury.aspu.ui.other.TopBarActivity
import com.merqury.aspu.ui.other.WebViewActivity
import com.merqury.aspu.ui.other.activityContentList
import com.merqury.aspu.ui.other.activityMap
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random
import kotlin.time.Duration


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

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

val contentList = mutableStateListOf<@Composable () -> Unit>()
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
        if (!showed.value) {
            onClosed()
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

fun goToScreen(activityClass: Class<*>) {
    appContext!!.startActivity(Intent(appContext!!, activityClass))
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
                        animationSpec = tween(
                            durationMillis =
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

fun async(runnable: () -> Unit) {
    Thread { runnable() }.start()
}

fun after(duration: Duration, runnable: () -> Unit) {
    async {
        Thread.sleep(duration.inWholeMilliseconds)
        runnable()
    }
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
        containerColor = SurfaceTheme.background.colorWithoutAnim
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
                            containerColor = SurfaceTheme.foreground.color
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
                                color = SurfaceTheme.text.color
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Context.startActivity(cls: Class<*>) {
    startActivity(Intent(this, cls))
}

fun Context?.startTopBarActivity(content: @Composable (topBar: MutableState<@Composable () -> Unit>) -> Unit) {
    startTopBarActivityWithActivityLink { header, _ ->
        content(header)
    }
}

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier, color: Color = Color.Black) {
    val flavour = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)
    val html = HtmlGenerator(text, parsedTree, flavour).generateHtml()
    HtmlText(text = html, color = color, modifier = modifier)
}

@Composable
fun HtmlText(text: String, modifier: Modifier = Modifier, color: Color = Color.Black) {
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                autoLinkMask = Linkify.WEB_URLS
                linksClickable = true
                setTextColor(color.toArgb())
            }
        },
        update = {
            it.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
fun EditableText(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true
){
    BasicTextField(
        value,
        onChange,
        enabled = enabled,
        textStyle = TextStyle(color = SurfaceTheme.text.color),
        cursorBrush = SolidColor(SurfaceTheme.text.color),
        decorationBox = { innerTextField ->
            Row(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    androidx.compose.material.Text(
                        text = placeholder,
                        color = SurfaceTheme.disable.color,
                        fontSize = 14.sp
                    )
                }
            }
            innerTextField()
        }
    )
}

fun Context?.startTopBarActivityWithActivityLink(content: @Composable (topBar: MutableState<@Composable () -> Unit>, activity: Activity?) -> Unit) {
    this!!
    var id = ""
    (1..10).forEach { _ ->
        id += Char(abs(Random.nextInt()) % 32768)
    }
    val intent = Intent(this, TopBarActivity::class.java)
    intent.putExtras(Bundle().apply {
        putString("activityId", id)
    })
    activityContentList.add {
        content(it, activityMap[id])
    }
    startActivity(intent)
}

@SuppressLint("SetJavaScriptEnabled")
fun showWebPage(url: String, scheme: String) {
    if (scheme in listOf("http", "https")) {
        WebViewActivity.url.value = "$scheme://$url"
        val intent = Intent(appContext, WebViewActivity::class.java)
        appContext!!.startActivity(intent)
    } else
        openInBrowser(url, scheme)
}

fun openInBrowser(url: String, scheme: String) {
    try {
        appContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$url")))
    } catch (e: ActivityNotFoundException) {
        appContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://$url")))
    }
    aspuButtonLoading.value = false
}

fun MutableState<Boolean>.toggle() {
    value = !value
}

@Composable
fun Modifier.placeholder(visible: Boolean = true): Modifier {
    return then(
        Modifier.placeholder(
            visible = visible,
            color = SurfaceTheme.placeholder_primary.color,
            highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
            shape = RoundedCornerShape(15.dp)
        )
    )
}

val Int.vw: Dp
    get() = ((appContext!!.resources.configuration.screenWidthDp.toDouble() / 100) * this).dp

val Double.vw: Dp
    get() = ((appContext!!.resources.configuration.screenWidthDp.toDouble() / 100) * this).dp

val Dp.px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.value,
        appContext!!.resources.displayMetrics
    )

val Dp.sp: TextUnit
    get() = (px / appContext!!.resources.displayMetrics.scaledDensity).sp


@Composable
fun TitleHeader(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = title,
            color = SurfaceTheme.text.color,
            fontSize = 24.sp,
            fontStyle = FontStyle.Italic
        )
    }
}

fun Context.makeToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
