package com.merqury.aspu.ui.navfragments.settings

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.merqury.aspu.ui.contentList
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color

class SettingsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            contentList.forEach {
                it()
            }
            SettingsScreen(header = remember {
                mutableStateOf({})
            })
            val foreground = SurfaceTheme.foreground.color
            window.statusBarColor =
                android.graphics.Color.rgb(foreground.red, foreground.green, foreground.blue)
            window.navigationBarColor =
                android.graphics.Color.rgb(foreground.red, foreground.green, foreground.blue)

            if (settingsPreferences.getString("theme", "light")!! == "light")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else
                window.decorView.systemUiVisibility = 0
        }
    }
}