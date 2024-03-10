package com.merqury.aspu.services

import android.content.Intent
import android.net.Uri
import com.merqury.aspu.context


fun sendToDevEmail(){
    context!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:petrakov.developer@gmail.com")))
}