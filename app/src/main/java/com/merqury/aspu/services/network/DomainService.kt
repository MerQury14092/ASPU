package com.merqury.aspu.services.network

import com.merqury.aspu.ui.async
import java.net.URI

fun getApiDomain(onComplete: (domain: String) -> Unit){
    async {
        try {
            val domain = URI("https://text-host.ru/raw/agpu-api-domain").toURL().readText()
            onComplete(domain)
        } catch (_: Exception){}
    }
}