package com.merqury.aspu.services

import androidx.compose.runtime.MutableState
import com.merqury.aspu.RUSTORE_RELEASE
import com.merqury.aspu.releaseType
import com.merqury.aspu.ui.async
import org.jsoup.Jsoup
import java.net.URI

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
    async {
        URI("https://apps.rustore.ru/app/com.merqury.aspu").toURL().readText()
        Thread.sleep(500)
        val response = URI("https://apps.rustore.ru/app/com.merqury.aspu").toURL().readText()
        val document = Jsoup.parse(response)

        version.value =
            document.getElementsByAttributeValue("itemprop", "softwareVersion").first()!!.text()
        releaseNotes.value =
            document.getElementsByAttributeValue("itemprop", "releaseNotes").first()?.text()
                ?: "Ничего"

    }
}