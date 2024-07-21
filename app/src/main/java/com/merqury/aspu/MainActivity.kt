package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.MainScreen
import com.merqury.aspu.ui.contentList
import com.merqury.aspu.ui.navfragments.settings.selectUser
import com.merqury.aspu.ui.navfragments.settings.selectableDisciplines
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import kotlinx.coroutines.CoroutineScope


@SuppressLint("StaticFieldLeak")
var appContext: Context? = null
var requestQueue: RequestQueue? = null
var _coroutineScope: CoroutineScope? = null
inline val mainCoroutineScope: CoroutineScope get() =  _coroutineScope!!
val apiDomain by lazy {
    AppConfig.getApiDomain()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContext = this
        requestQueue = Volley.newRequestQueue(appContext)
        setContent {
            Text(text = "Hello world")
            _coroutineScope = rememberCoroutineScope()
            contentList.forEach {
                it()
            }
            if (AppSettings.firstLaunch)
                FirstStart()
            MainScreen()
            val foreground = SurfaceTheme.foreground.color
            window.statusBarColor =
                android.graphics.Color.rgb(foreground.red, foreground.green, foreground.blue)
            window.navigationBarColor =
                android.graphics.Color.rgb(foreground.red, foreground.green, foreground.blue)

            if (AppSettings.selectedTheme == "light")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else
                window.decorView.systemUiVisibility = 0
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

@Composable
fun FirstStart() {
    val userSelected = remember {
        mutableStateOf(false)
    }
    val userSelectShow = remember {
        mutableStateOf(false)
    }
    val idSelectShow = remember {
        mutableStateOf(false)
    }
    if (!userSelected.value && !userSelectShow.value) {
        userSelectShow.value = true
        selectUser(userSelected)
    }
    if (userSelected.value && !idSelectShow.value) {
        idSelectShow.value = true
        showSelectIdModalWindow(
            timetableId = AppSettings.timetableId,
            filteredBy = when (AppSettings.whoIsUser) {
                "student" -> "group"
                "teacher" -> "teacher"
                else -> "group"
            }
        ) {
            AppSettings.timetableId = it.searchContent
            AppSettings.timetableIdOwner = it.type.uppercase()
            selectableDisciplines.edit().clear().apply()
        }
        AppSettings.firstLaunch = false
    }
}
