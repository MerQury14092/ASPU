package com.merqury.aspu.services.marks.models


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue



data class MarksResponse (
    val data: Data? = null,
    val state: Long? = null,
    val msg: String? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        fun fromJson(json: String) = mapper.readValue<MarksResponse>(json)
        val mapper = jacksonObjectMapper().apply {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    data class Data (
        val showVedButton: Boolean? = null,
        val showPrintForm: Boolean? = null,
        val showPersonalCard: Boolean? = null,
        val groupID: Long? = null,
        val markCountStatistic: List<MarkCountStatistic>? = null,
        val avgCourseStatistic: List<AvgCourseStatistic>? = null,
        val zachBook: List<ZachBook>? = null,
        val groupedZachBook: List<GroupedZachBook>? = null,
        val studentName: String? = null,
        val recordbook: String? = null,
        val studentInfo: StudentInfo? = null,
        val avgPoint: Long? = null,
        val currentSem: Long? = null,
        val photo: String? = null,

        @get:JsonProperty("isZaoch")@field:JsonProperty("isZaoch")
        val isZaoch: Boolean? = null
    )

    data class AvgCourseStatistic (
        val course: Long? = null,
        val avg: Long? = null
    )

    data class GroupedZachBook (
        val key: String? = null,
        val year: String? = null,
        val session: Long? = null,
        val course: Long? = null,
        val sem: Long? = null,
        val controlForm: String? = null,
        val marks: List<ZachBook>? = null,
        val order: Long? = null
    )

    data class ZachBook (
        val key: Long? = null,
        val course: Long? = null,
        val sem: Long? = null,
        val session: Long? = null,
        val dis: String? = null,
        val mark: String? = null,
        val hours: Long? = null,
        val vedID: Long? = null,
        val block: String? = null,
        val controlForm: String? = null,
        val date: String? = null,
        val teacherName: String? = null,
        val year: String? = null,
        val markNumber: Long? = null,
        val zet: Long? = null,
        val closed: Boolean? = null
    )

    data class MarkCountStatistic (
        val mark: String? = null,
        val count: Long? = null,
        val percent: Double? = null
    )

    data class StudentInfo (
        val name: String? = null,
        val group: String? = null,
        val specialty: String? = null
    )

}

