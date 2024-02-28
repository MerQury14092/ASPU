package com.merqury.aspu.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview(showBackground = true)
fun prev() {
    answerContent(name = "Лабара", mutableStateOf(false))
}

@Composable
fun answerContent(name: String, it: MutableState<Boolean>) {

}