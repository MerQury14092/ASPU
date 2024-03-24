package com.merqury.aspu.services.profile.models
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue



data class MarkStat (
    val data: Data? = null,
    val state: Long? = null,
    val msg: String? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<MarkStat>(json)
    }
    data class Data (
        val markCountStatistic: List<MarkCountStatistic>? = null,
        val count: Long? = null
    )

    data class MarkCountStatistic (
        val mark: Long,
        val markName: String,
        val count: Long,
        val avg: Double
    )
}


