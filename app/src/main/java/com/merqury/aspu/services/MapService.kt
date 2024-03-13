package com.merqury.aspu.services

import android.content.Intent
import android.net.Uri
import com.merqury.aspu.appContext

fun openMapWithMarker(lat: String, lon: String, label: String){
    val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon($label)")
    appContext!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
}