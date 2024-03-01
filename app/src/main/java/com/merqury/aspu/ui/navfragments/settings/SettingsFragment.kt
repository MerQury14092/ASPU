package com.merqury.aspu.ui.navfragments.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun SettingsChapter(title: String, buttons: Map<String, () -> Unit>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = theme.value[SurfaceTheme.text]!!)
        buttons.entries.forEach {
            if (it.key.isNotEmpty())
                SettingsButton(onClick = it.value) {
                    Text(text = it.key, fontSize = 16.sp, color = theme.value[SurfaceTheme.text]!!)
                }
        }
        Divider(color = theme.value[SurfaceTheme.divider]!!)
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
            containerColor = theme.value[SurfaceTheme.foreground]!!
        )
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            content()
        }
    }
}