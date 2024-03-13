package com.merqury.aspu.services

import com.merqury.aspu.ui.async
import java.net.URI

fun getApiDomain(onComplete: (domain: String) -> Unit){
    async {
        val domain = URI("https://text-host.ru/raw/agpu-api-domain").toURL().readText()
        onComplete(domain)
    }
}