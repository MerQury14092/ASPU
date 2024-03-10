package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.merqury.aspu.ui.MainScreen
import com.merqury.aspu.ui.navfragments.settings.reloadSettingsScreen
import com.merqury.aspu.ui.navfragments.settings.selectUser
import com.merqury.aspu.ui.navfragments.settings.selectableDisciplines
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.showSelectIdModalWindow


@SuppressLint("StaticFieldLeak")
var context: Context? = null
var requestQueue: RequestQueue? = null
var pInfo: PackageInfo? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        context = this
        pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
        requestQueue = Volley.newRequestQueue(context)
        setContent {
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

private val contentList = mutableStateListOf<@Composable () -> Unit>()
fun show(
    visibility: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    contentList.add(content)
}

fun close(
    content: @Composable () -> Unit
){

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
