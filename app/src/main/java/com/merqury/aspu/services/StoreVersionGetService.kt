package com.merqury.aspu.services

import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.RUSTORE_RELEASE
import com.merqury.aspu.releaseType
import com.merqury.aspu.requestQueue
import org.jsoup.Jsoup

fun getLastPublishedVersion(
    version: MutableState<String>,
    releaseNotes: MutableState<String>
) {
    if (releaseType == RUSTORE_RELEASE)
        getLastPublishedVersionRustore(version, releaseNotes)
}

fun getLastPublishedVersionRustore(
    version: MutableState<String>,
    releaseNotes: MutableState<String>
) {
    val url = "https://apps.rustore.ru/app/com.merqury.aspu"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            val document = Jsoup.parse(it)
            version.value =
                document.getElementsByAttributeValue("itemprop", "softwareVersion").first()?.text()
                    ?: "UNKNOWN"
            releaseNotes.value =
                document.getElementsByAttributeValue("itemprop", "releaseNotes").first()?.text()
                    ?: "Ничего"
        },
        {
            version.value = "UNKNOWN"
        }
    )
    requestQueue!!.add(request)
}