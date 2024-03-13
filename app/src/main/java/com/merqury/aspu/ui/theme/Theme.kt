package com.merqury.aspu.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.merqury.aspu.appContext
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences

enum class SurfaceTheme {
    background,
    foreground,
    divider,
    disable,
    enable,
    text,
    button
}

const val themeChangeDuration = 300

val lightTheme = mapOf(
    SurfaceTheme.background to Color.White,
    SurfaceTheme.foreground to Color(0xffedeef0),
    SurfaceTheme.divider to Color(0xffdee1e6),
    SurfaceTheme.disable to Color(0xff656F7E),
    SurfaceTheme.enable to Color.Black,
    SurfaceTheme.text to Color.Black,
    SurfaceTheme.button to Color(0xffd4d4d4)
)

val darkTheme = mapOf(
    SurfaceTheme.background to Color(0xff141414),
    SurfaceTheme.foreground to Color(0xff222222),
    SurfaceTheme.divider to Color(0xff1f1f1f),
    SurfaceTheme.disable to Color(0xff939393),
    SurfaceTheme.enable to Color.White,
    SurfaceTheme.text to Color(0xffE2E3E6),
    SurfaceTheme.button to Color(0xff2b2b2b)
)

var theme = mutableStateOf(darkTheme)
/*var theme = mutableStateOf(
    if (settingsPreferences.getString("theme",
            if(appContext!!.isDarkThemeOn())
                "dark"
            else
                "light"
        ) == "dark") darkTheme else lightTheme
)*/

fun updateTheme(){
    theme.value =
        if (settingsPreferences.getString("theme",
                if(appContext!!.isDarkThemeOn())
                    "dark"
                else
                    "light"
            ) == "dark") darkTheme else lightTheme
}

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}