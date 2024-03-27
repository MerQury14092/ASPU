package com.merqury.aspu.services.timetable.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue



class FacultiesList(elements: Collection<FacultiesListElement>) : ArrayList<FacultiesListElement>(elements) {
    companion object {
        private val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<FacultiesList>(json)
    }
}

data class FacultiesListElement (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val facultyName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val groups: List<String>
)
