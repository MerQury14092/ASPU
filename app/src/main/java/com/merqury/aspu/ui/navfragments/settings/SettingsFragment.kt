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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import com.merqury.aspu.ui.theme.themeChangeDuration

@Composable
fun SettingsChapter(title: String, buttons: Map<String, () -> Unit>) {
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
        buttons.entries.forEach {
            if (it.key.isNotEmpty())
                SettingsButton(onClick = it.value) {
                    Text(
                        text = it.key,
                        fontSize = 16.sp,
                        color = animateColorAsState(
                            targetValue = theme.value[SurfaceTheme.text]!!,
                            animationSpec = tween(durationMillis = themeChangeDuration),
                            label = ""
                        ).value
                    )
                }
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Divider(color = animateColorAsState(
            targetValue = theme.value[SurfaceTheme.divider]!!,
            animationSpec = tween(durationMillis = themeChangeDuration),
            label = ""
        ).value)
    }
}


@Composable
fun SettingsButton(onClick: () -> Unit, content: @Composable () -> Unit) {
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
            content()
        }
    }
}