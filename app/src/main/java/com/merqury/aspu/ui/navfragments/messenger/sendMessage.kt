package com.merqury.aspu.ui.navfragments.messenger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.merqury.aspu.ui.TitleHeader

@Composable
fun SendMessageScreen(header: MutableState<@Composable () -> Unit>){
    header.value = {
        TitleHeader(title = "Пидор")
    }
}