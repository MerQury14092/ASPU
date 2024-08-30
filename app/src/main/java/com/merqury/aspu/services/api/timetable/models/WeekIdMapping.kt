package com.merqury.aspu.services.api.timetable.models

import com.fasterxml.jackson.annotation.JsonProperty

data class WeekIdMapping(
    val range: String,
    @JsonProperty("id_mapping")
    val id: Int
)
