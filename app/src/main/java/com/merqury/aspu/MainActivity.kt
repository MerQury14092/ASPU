package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.merqury.aspu.services.getApiDomain
import com.merqury.aspu.services.getLastPublishedVersion
import com.merqury.aspu.ui.MainScreen
import com.merqury.aspu.ui.navfragments.settings.reloadSettingsScreen
import com.merqury.aspu.ui.navfragments.settings.selectUser
import com.merqury.aspu.ui.navfragments.settings.selectableDisciplines
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color


@SuppressLint("StaticFieldLeak")
var appContext: Context? = null
var requestQueue: RequestQueue? = null
var appVersion: String? = null
const val RUSTORE_RELEASE = "rustore"
const val PLAYMARKET_RELEASE = "google"
const val releaseType = PLAYMARKET_RELEASE
private val storeAppVersion = mutableStateOf("UNKNOWN")
private val storeAppReleaseNotes = mutableStateOf("")
var apiDomain = "agpu.merqury.fun"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApiDomain {
            if(it != apiDomain)
                apiDomain = it
        }
        appContext = this
        appVersion = appContext!!.packageManager.getPackageInfo(appContext!!.packageName, 0).versionName
        requestQueue = Volley.newRequestQueue(appContext)
        getLastPublishedVersion(storeAppVersion, storeAppReleaseNotes)
        setContent {
            if (storeAppVersion.value != "UNKNOWN" && storeAppVersionBigger())
                NewVersionNotification()
            contentList.forEach {
                it()
            }
            if (settingsPreferences.getBoolean("first_launch", true))
                FirstStart()
            MainScreen()
        }
    }

    override fun onPause() {
        super.onPause()
        contentList.clear()
    }
}

fun storeAppVersionBigger(): Boolean {
    val sav = storeAppVersion.value.replace(Regex("[^0-9.]"), "").toDouble()
    val cav = appVersion!!.replace(Regex("[^0-9.]"), "").toDouble()
    return sav > cav
}

@Composable
private fun NewVersionNotification() {
    showSimpleModalWindow(
        containerColor = SurfaceTheme.background.color
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Column {
                Text(
                    text = "В RUSTORE вышла новая версия! Скорее обновите!",
                    color = SurfaceTheme.text.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(10.dp))
                Divider(color = SurfaceTheme.divider.color)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Ваша версия: $appVersion", color = SurfaceTheme.text.color)
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Новая версия: ${storeAppVersion.value}",
                    color = SurfaceTheme.text.color
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Что нового:",
                    color = SurfaceTheme.text.color,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = storeAppReleaseNotes.value, color = SurfaceTheme.text.color)
                Spacer(modifier = Modifier.size(10.dp))
                Divider(color = SurfaceTheme.divider.color)
                Spacer(modifier = Modifier.size(10.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                    Button(
                        onClick = { it.value = false }, colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceTheme.button.color
                        )
                    ) {
                        Text(text = "Хорошо", color = SurfaceTheme.text.color)
                    }
                }
            }
        }
    }
}

val contentList = mutableStateListOf<@Composable () -> Unit>()
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
            filteredBy = when (settingsPreferences.getString("user", "student")) {
                "student" -> "group"
                "teacher" -> "teacher"
                else -> "group"
            }
        ) {
            settingsPreferences.edit().putString("timetable_id", it.searchContent)
                .apply()
            settingsPreferences.edit()
                .putString("timetable_id_owner", it.type.uppercase())
                .apply()
            selectableDisciplines.edit().clear().apply()
            reloadSettingsScreen()
        }
        settingsPreferences.edit().putBoolean("first_launch", false).apply()
    }
}
