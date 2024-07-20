package com.merqury.aspu.ui.training

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.IntroShowcaseScope
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.colorWithoutAnim

@SuppressLint("ModifierFactoryExtensionFunction")
fun IntroShowcaseScope.hintTargetModifier(
    index: Int,
    title: String,
    description: String
): Modifier {
    return Modifier.introShowCaseTarget(
        index,
        style = showcaseStyle,
        content = showcaseContent(title, description)
    )
}

private val showcaseStyle = ShowcaseStyle.Default.copy(
    backgroundColor = SurfaceTheme.foreground.colorWithoutAnim,
    backgroundAlpha = 0.98f,
    targetCircleColor = SurfaceTheme.text.colorWithoutAnim
)

private fun showcaseContent(title: String, description: String): @Composable BoxScope.() -> Unit = {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(10.dp))
        ThemeText(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        ThemeText(
            text = description,
            fontSize = 16.sp
        )
    }
}