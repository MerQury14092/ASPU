package com.merqury.aspu.services.intents

import android.content.Intent
import android.net.Uri
import com.merqury.aspu.appContext


fun sendToDevEmail(){
    appContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:petrakov.developer@gmail.com")))
}