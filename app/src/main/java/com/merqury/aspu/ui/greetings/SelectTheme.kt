package com.merqury.aspu.ui.greetings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.AutoSizeThemeText
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.getThemeName
import com.merqury.aspu.ui.theme.themes

@Composable
fun SelectTheme() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.aspu), contentDescription = null)
            Spacer(modifier = Modifier.size(50.dp))
            ThemeText(
                text = "Тема приложения",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                themes.forEach { (theme, icon) ->
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        SelectThemeBox(theme, icon)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectThemeBox(theme: String, icon: Int) {
    Box(modifier = Modifier.bounceClick {
        AppSettings.selectedTheme = theme
    }) {
        Box(
            modifier = Modifier
                .background(SurfaceTheme.button.color, shape = RoundedCornerShape(10.dp))
                .padding(5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(.66f)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = SurfaceTheme.text.color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    AutoSizeThemeText(text = getThemeName(theme))
                }

                Spacer(modifier = Modifier.size(10.dp))
                if (AppSettings.selectedTheme == theme) {
                    Image(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            SurfaceTheme.text.color
                        ),
                        modifier = Modifier.size(15.dp)
                    )
                } else
                    Spacer(modifier = Modifier.size(15.dp))
            }
        }
    }
}