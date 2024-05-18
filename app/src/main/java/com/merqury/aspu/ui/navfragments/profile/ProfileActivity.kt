package com.merqury.aspu.ui.navfragments.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.merqury.aspu.ui.contentList

class ProfileActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            contentList.forEach {
                it()
            }
            ProfileScreen(header = remember {
                mutableStateOf({})
            })
        }
    }
}