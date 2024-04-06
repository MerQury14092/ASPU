package com.merqury.aspu.services.mailbox.outbox.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class PrepodFioModel (
    val data: Data? = null,
    val state: Long? = null,
    val msg: String? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<PrepodFioModel>(json)
    }
    data class Data (
        val arrPrep: List<FioSearchElement>? = null
    )
}


