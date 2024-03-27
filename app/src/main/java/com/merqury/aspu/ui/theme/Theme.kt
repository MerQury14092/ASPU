package com.merqury.aspu.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
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
    button,
    placeholder_primary,
    placeholder_secondary,
    appBars,
}

private const val themeChangeDuration = 300

private val lightTheme = mapOf(
    SurfaceTheme.background to Color.White,
    SurfaceTheme.foreground to Color(0xffedeef0),
    SurfaceTheme.appBars to Color(0xffedeef0),
    SurfaceTheme.placeholder_primary to Color(0xFFD4D6D8),
    SurfaceTheme.placeholder_secondary to Color(0xFFBCBEC0),
    SurfaceTheme.divider to Color(0xffdee1e6),
    SurfaceTheme.disable to Color(0xff656F7E),
    SurfaceTheme.text to Color.Black,
    SurfaceTheme.enable to Color.Black,
    SurfaceTheme.button to Color(0xffd4d4d4)
)

private val darkTheme = mapOf(
    SurfaceTheme.background to Color(0xff141414),
    SurfaceTheme.foreground to Color(0xff222222),
    SurfaceTheme.appBars to Color(0xff222222),
    SurfaceTheme.placeholder_primary to Color(0xFF252525),
    SurfaceTheme.placeholder_secondary to Color(0xFF302F2F),
    SurfaceTheme.divider to Color(0xff1f1f1f),
    SurfaceTheme.disable to Color(0xff939393),
    SurfaceTheme.enable to Color.White,
    SurfaceTheme.text to Color(0xffE2E3E6),
    SurfaceTheme.button to Color(0xff2b2b2b)
)

private val seaTheme = mapOf(
    SurfaceTheme.background to Color(0xff252850),
    SurfaceTheme.foreground to Color(0xff2271B3),
    SurfaceTheme.appBars to Color(0xff2271B3),
    SurfaceTheme.placeholder_primary to Color(0xFF0095B6),
    SurfaceTheme.placeholder_secondary to Color(0xFF80DAEB),
    SurfaceTheme.divider to Color(0xff002F55),
    SurfaceTheme.disable to Color(0xff79A0C1),
    SurfaceTheme.enable to Color(0xffF0F8FF),
    SurfaceTheme.text to Color(0xffF0F8FF),
    SurfaceTheme.button to Color(0xff1A4780)
)


// TODO: site theme
private val aspuSiteTheme = mapOf(
    SurfaceTheme.background to Color.White,
    SurfaceTheme.foreground to Color(0xFF9DE3E7),
    SurfaceTheme.appBars to Color(0xff00C6D2),
    SurfaceTheme.placeholder_primary to Color(0xFF9DE3E7),
    SurfaceTheme.placeholder_secondary to Color(0xFF80DAEB),
    SurfaceTheme.divider to Color(0xFF03929B),
    SurfaceTheme.disable to Color(0xFF2E2D2D),
    SurfaceTheme.enable to Color(0xFF030F1A),
    SurfaceTheme.text to Color(0xFF060C11),
    SurfaceTheme.button to Color(0xFF0AA3AD)
)

private var theme = mutableStateOf(
    byName(settingsPreferences.getString("theme",
        if(appContext?.isDarkThemeOn() != false)
            "dark"
        else
            "light"
    )!!)
)

private fun byName(name: String): Map<SurfaceTheme, Color>{
    return when(name){
        "sea" -> seaTheme
        "dark" -> darkTheme
        "site" -> aspuSiteTheme
        else -> lightTheme
    }
}

fun getThemeName(name: String): String{
    return when(name){
        "sea" -> "Морская"
        "dark" -> "Тёмная"
        "site" -> "Лазурная"
        else -> "Светлая"
    }
}

val SurfaceTheme.color: Color
    @Composable get() = theme.value[this]!!.animatedColorOnThemeChange()

val SurfaceTheme.colorWithoutAnim
    get() = theme.value[this]!!

fun updateTheme(){
    theme.value = byName(settingsPreferences.getString("theme",
        if(appContext!!.isDarkThemeOn())
            "dark"
        else
            "light"
    )!!)
}

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}

@Composable
private fun Color.animatedColorOnThemeChange(): Color {
    return animateColorAsState(
        targetValue = this,
        animationSpec = tween(durationMillis = themeChangeDuration),
        label = ""
    ).value
}