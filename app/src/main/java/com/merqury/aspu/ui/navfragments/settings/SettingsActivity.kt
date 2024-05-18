package com.merqury.aspu.ui.navfragments.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.merqury.aspu.ui.contentList

class SettingsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            contentList.forEach {
                it()
            }
            SettingsScreen(header = remember {
                mutableStateOf({})
            })
        }
    }
}