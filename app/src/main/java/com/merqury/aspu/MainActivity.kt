package com.merqury.aspu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.merqury.aspu.ui.MainScreen

@SuppressLint("StaticFieldLeak")
var context: Context? = null
var requestQueue: RequestQueue? = null
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        requestQueue = Volley.newRequestQueue(context)
        setContent {
            MainScreen()
        }
    }
}
