package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.merqury.aspu.ui.MainScreen

@SuppressLint("StaticFieldLeak")
var mainContext: Context? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainContext = this
        setContent {
            MainScreen()
        }
    }
}