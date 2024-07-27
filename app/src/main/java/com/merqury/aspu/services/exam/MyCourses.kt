package com.merqury.aspu.services.exam

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue



class MyCourses(elements: Collection<MyCourse>) : ArrayList<MyCourses.MyCourse>(elements) {
    fun toJson() = mapper.writeValueAsString(this)
    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        fun fromJson(json: String) = mapper.readValue<MyCourses>(json)
    }
    data class MyCourse (
        val error: Boolean? = null,
        val data: Data? = null
    )

    data class Data (
        val courses: List<Course>? = null,
        val nextoffset: Long? = null
    )

    data class Course (
        val id: Long? = null,
        val fullname: String? = null,
        val shortname: String? = null,
        val idnumber: String? = null,
        val summary: String? = null,
        val summaryformat: Long? = null,
        val startdate: Long? = null,
        val enddate: Long? = null,
        val visible: Boolean? = null,
        val showactivitydates: Boolean? = null,
        val showcompletionconditions: Boolean? = null,
        val pdfexportfont: String? = null,
        val fullnamedisplay: String? = null,
        val viewurl: String? = null,
        val progress: Long? = null,
        val hasprogress: Boolean? = null,

        @get:JsonProperty("isfavourite")@field:JsonProperty("isfavourite")
        val isfavourite: Boolean? = null,

        val hidden: Boolean? = null,
        val showshortname: Boolean? = null,
        val coursecategory: String? = null
    )
}