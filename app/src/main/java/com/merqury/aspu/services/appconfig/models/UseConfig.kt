package com.merqury.aspu.services.appconfig.models

data class UseConfig(
    val id: Long,
    val versions: String,
    val canUse: Boolean,
    val reason: String?
)