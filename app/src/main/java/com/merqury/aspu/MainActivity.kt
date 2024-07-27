package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.appconfig.models.AnnouncementType
import com.merqury.aspu.ui.ColorizeAppBars
import com.merqury.aspu.ui.MainScreen
import com.merqury.aspu.ui.contentList
import com.merqury.aspu.ui.other.showAnnouncement
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import kotlinx.coroutines.CoroutineScope


@SuppressLint("StaticFieldLeak")
var appContext: Context? = null
var requestQueue: RequestQueue? = null
var _coroutineScope: CoroutineScope? = null
inline val mainCoroutineScope: CoroutineScope get() = _coroutineScope!!
val apiDomain by lazy {
    AppConfig.getApiDomain()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContext = this
        requestQueue = Volley.newRequestQueue(appContext)
        var firstComposition by mutableStateOf(true)
        val announcements = AppConfig.getAnnouncements()

        setContent {
            if (firstComposition) {
                firstComposition = false
                announcements.forEach {
                    showAnnouncement(it)
                }
            }
            _coroutineScope = rememberCoroutineScope()
            contentList.forEach {
                it()
            }
            ColorizeAppBars(window = window, SurfaceTheme.foreground.color)
            if (announcements.any { it.type == AnnouncementType.blocking }) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceTheme.background.color))
                return@setContent
            }
//            if (AppConfig.internetAccess)
                MainScreen()
//            else
//                CachedTimetable()
        }
    }
}

fun show(
    visibility: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    contentList.add(content)
}

fun close(
    content: @Composable () -> Unit
) {

}