package com.merqury.aspu.services.mailbox.outbox.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class AnyData(
    val data: Any? = null,
    val state: Long? = null,
    val msg: String? = null
) {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<AnyData>(json)
    }
}