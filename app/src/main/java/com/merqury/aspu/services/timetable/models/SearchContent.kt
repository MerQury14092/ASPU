package com.merqury.aspu.services.timetable.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue



class SearchContent(elements: Collection<SearchContentElement>) : ArrayList<SearchContentElement>(elements) {
    companion object {
        private val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<SearchContent>(json)
    }
}

data class SearchContentElement (
    @get:JsonProperty("SearchContent", required=true)@field:JsonProperty("SearchContent", required=true)
    var searchContent: String,

    @get:JsonProperty("Type", required=true)@field:JsonProperty("Type", required=true)
    val type: String,

    @get:JsonProperty("SearchId", required=true)@field:JsonProperty("SearchId", required=true)
    val searchID: Long,

    @get:JsonProperty("OwnerId", required=true)@field:JsonProperty("OwnerId", required=true)
    val ownerID: Long
)