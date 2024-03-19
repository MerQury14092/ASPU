package com.merqury.aspu.ui.navfragments.timetable.DTO

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.merqury.aspu.services.fetchFullFio


data class TimetableDay (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val date: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val id: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val owner: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val disciplines: List<Discipline>
) {

    companion object {
        private val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String): TimetableDay {
            val res = mapper.readValue<TimetableDay>(json)
            res.disciplines.forEach {
                it.teacherName.fetchFullFio()
            }
            return res
        }

        fun TimetableDay.toJson(): String {
            return mapper.writeValueAsString(this)
        }
    }
}

data class Discipline (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val time: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    var name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val teacherName: String,

    @get:JsonProperty("audienceId", required=true)@field:JsonProperty("audienceId", required=true)
    val audienceID: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val subgroup: Int,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val type: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val groupName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val distant: Boolean
)