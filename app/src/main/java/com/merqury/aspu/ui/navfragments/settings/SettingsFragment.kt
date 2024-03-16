package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.ui.navBarUpdate
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.animatedColorOnThemeChange
import com.merqury.aspu.ui.theme.theme
import com.merqury.aspu.ui.theme.themeChangeDuration

abstract class SettingsButton(var text: String) {
    abstract fun getContent(): @Composable () -> Unit;
}

class SwitchableSettingsPreferenceButton(text: String, private val settingsPreferenceBooleanName: String) :
    SettingsButton(text) {
    override fun getContent() = @Composable {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            colors = CardDefaults.cardColors(
                containerColor = animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.foreground]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value
            )
        ) {
            Box(contentAlignment = Alignment.CenterStart) {
                Box(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        color = animateColorAsState(
                            targetValue = theme.value[SurfaceTheme.text]!!,
                            animationSpec = tween(durationMillis = themeChangeDuration),
                            label = ""
                        ).value
                    )
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp), contentAlignment = Alignment.TopEnd) {
                    Switch(
                        checked = settingsPreferences.getBoolean(
                            settingsPreferenceBooleanName,
                            false
                        ), onCheckedChange = {
                            toggleBooleanSettingsPreference(settingsPreferenceBooleanName)
                            reloadSettingsScreen()
                            navBarUpdate()
                        },
                        modifier = Modifier.scale(.75f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = theme.value[SurfaceTheme.text]!!.animatedColorOnThemeChange(),
                            checkedBorderColor = theme.value[SurfaceTheme.text]!!.animatedColorOnThemeChange(),
                            checkedTrackColor = theme.value[SurfaceTheme.foreground]!!.animatedColorOnThemeChange(),
                            uncheckedThumbColor = theme.value[SurfaceTheme.disable]!!.animatedColorOnThemeChange(),
                            uncheckedBorderColor = theme.value[SurfaceTheme.text]!!.animatedColorOnThemeChange(),
                            uncheckedTrackColor = theme.value[SurfaceTheme.foreground]!!.animatedColorOnThemeChange(),
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsButton(onClick: () -> Unit, content: @Composable () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = animateColorAsState(
                targetValue = theme.value[SurfaceTheme.foreground]!!,
                animationSpec = tween(durationMillis = themeChangeDuration),
                label = ""
            ).value
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    onClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.foreground]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value
            )
        ) {
            content()
        }
    }
}

class ClickableSettingsButton(text: String, val onClick: () -> Unit) : SettingsButton(text) {
    override fun getContent() = @Composable {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .clickable {
                    onClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = animateColorAsState(
                    targetValue = theme.value[SurfaceTheme.foreground]!!,
                    animationSpec = tween(durationMillis = themeChangeDuration),
                    label = ""
                ).value
            )
        ) {
            Box(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = animateColorAsState(
                        targetValue = theme.value[SurfaceTheme.text]!!,
                        animationSpec = tween(durationMillis = themeChangeDuration),
                        label = ""
                    ).value
                )
            }
        }
    }
}

@Composable
fun SettingsChapter(title: String, buttons: List<SettingsButton>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title, color = animateColorAsState(
                targetValue = theme.value[SurfaceTheme.text]!!,
                animationSpec = tween(durationMillis = themeChangeDuration),
                label = ""
            ).value
        )
        buttons.forEach {
            it.getContent()()
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Divider(
            color = animateColorAsState(
                targetValue = theme.value[SurfaceTheme.divider]!!,
                animationSpec = tween(durationMillis = themeChangeDuration),
                label = ""
            ).value
        )
    }
}