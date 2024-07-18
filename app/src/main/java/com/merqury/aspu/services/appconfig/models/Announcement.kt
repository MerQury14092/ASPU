package com.merqury.aspu.services.appconfig.models

import com.fasterxml.jackson.module.kotlin.readValue
import com.merqury.aspu.services.appconfig.mapper

data class Announcement (
    val id: Long,
    val versions: String,
    val blockTheStart: Boolean,
    val title: String,
    val body: String
) {
    companion object {
        fun fromJson(json: String): Announcement {
            return mapper.readValue<Announcement>(json)
        }
    }
}