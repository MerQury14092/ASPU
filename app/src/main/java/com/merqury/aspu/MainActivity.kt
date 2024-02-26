package com.merqury.aspu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.merqury.aspu.ui.MainScreen

var requestQueue: RequestQueue? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestQueue = Volley.newRequestQueue(this)
        setContent {
            MainScreen()
        }
    }
}