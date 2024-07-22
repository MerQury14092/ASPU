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
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import com.merqury.aspu.ui.bounceClick
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.show
import com.merqury.aspu.ui.other.TopBarActivity
import com.merqury.aspu.ui.other.WebViewActivity
import com.merqury.aspu.ui.other.activityContentList
import com.merqury.aspu.ui.other.activityMap
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


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

enum class ButtonState { Pressed, Idle }

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("ReturnFromAwaitPointerEventScope")
fun Modifier.bounceClick(
    onLongClick: () -> Unit = {},
    onClick: () -> Unit
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        if (buttonState == ButtonState.Pressed) 0.85f else 1f,
        label = ""
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
            onLongClick = onLongClick
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
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


private val executor = Executors.newFixedThreadPool(12)
fun async(runnable: () -> Unit) {
    executor.submit(runnable)
}

fun after(duration: Duration, runnable: () -> Unit) {
    async {
        Thread.sleep(duration.inWholeMilliseconds)
        runnable()
    }
}

// dp(Dp) → px(Float)
@Composable
internal fun Dp.toPx(): Float {
    return this.value * LocalDensity.current.density
}

// dp(Dp) → sp(TextUnit)
@Composable
internal fun Dp.toSp(): TextUnit {
    return (this.value * LocalDensity.current.density / LocalDensity.current.fontScale).sp
}

// px(Float) → dp(Dp)
@Composable
internal fun Float.toDp(): Dp {
    return (this / LocalDensity.current.density).dp
}

// px(Float) → sp(TextUnit)
@Composable
internal fun Float.toSp(): TextUnit {
    return (this / LocalDensity.current.fontScale).sp
}

// sp(TextUnit) → dp(Dp)
@Composable
internal fun TextUnit.toDp(): Dp {
    return (this.value * LocalDensity.current.fontScale / LocalDensity.current.density).dp
}

// sp(TextUnit) → px(Float)
@Composable
internal fun TextUnit.toPx(): Float {
    return this.value * LocalDensity.current.fontScale
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

@Composable
fun ColorizeAppBars(window: Window, color: Color) {
    window.statusBarColor =
        android.graphics.Color.rgb(color.red, color.green, color.blue)
    window.navigationBarColor =
        android.graphics.Color.rgb(color.red, color.green, color.blue)
    if (AppSettings.selectedTheme == "light")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    else
        window.decorView.systemUiVisibility = 0
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
                                .bounceClick {
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

fun showSelectListDialogWithClickAnimation(
    buttons: Map<String, (MutableState<Boolean>) -> Unit>,
    sortedByAlphabet: Boolean = false
) {
    showSelectListDialogWithClickAnimation(
        mutableStateOf(buttons),
        sortedByAlphabet
    )
}

fun showSelectListDialogWithClickAnimation(
    buttons: MutableState<Map<String, (MutableState<Boolean>) -> Unit>>,
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
            var isLoading = remember {
                mutableStateListOf<String>()
            }
            val isDone = remember {
                mutableStateOf(false)
            }
            if (isDone.value)
                modalWindowVisibility.value = false
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
                                .bounceClick {
                                    it.value(isDone)
                                    isLoading.add(it.key)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading.contains(it.key))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = SurfaceTheme.text.color
                                )
                            else
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

@Volatile
var loadingWindowClosed = false

fun showLoadingModalWindow(
    title: MutableState<String>,
    success: MutableState<Boolean?>,
    afterClosing: () -> Unit = {}
) {
    loadingWindowClosed = false
    showSimpleModalWindow(
        containerColor = SurfaceTheme.background.colorWithoutAnim,
        closeable = false
    ) {
        if (success.value != null) {
            if (success.value!!) {
                Box(modifier = Modifier.padding(30.dp), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            Icons.Rounded.Check,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                SurfaceTheme.text.color
                            )
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        ThemeText(
                            text = title.value,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.padding(30.dp), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            Icons.Rounded.Warning,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                SurfaceTheme.text.color
                            )
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        ThemeText(
                            text = title.value,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            async {
                after(2.seconds) {
                    it.value = false
                    after(Random.nextDouble(0.0, 0.5).seconds) {
                        if (!loadingWindowClosed) {
                            afterClosing()
                            loadingWindowClosed = true
                        }
                    }
                }
            }
        } else
            Box(modifier = Modifier.padding(30.dp), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = SurfaceTheme.text.color)
                    Spacer(modifier = Modifier.size(20.dp))
                    ThemeText(
                        text = title.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
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
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true
) {
    BasicTextField(
        value,
        onChange,
        enabled = enabled,
        modifier = modifier,
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
