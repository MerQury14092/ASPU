package com.merqury.aspu.services.appconfig.models

import com.fasterxml.jackson.module.kotlin.readValue
import com.merqury.aspu.services.appconfig.mapper

enum class AnnouncementType {
    simple,
    blocking,
    intrusive
}

data class Announcement (
    val id: Long,
    val versions: String,
    val updateRequest: Boolean,
    val type: AnnouncementType,
    val title: String,
    val body: String
) {
    companion object {
        fun fromJson(json: String): Announcement {
            return mapper.readValue<Announcement>(json)
        }
    }
}