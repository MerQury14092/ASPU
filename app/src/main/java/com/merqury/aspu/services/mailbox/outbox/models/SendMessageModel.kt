package com.merqury.aspu.services.mailbox.outbox.models

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.*



data class SendMessageModel (
    val markdownMessage: String? = "",
    val htmlMessage: String? = "",
    val message: String? = "",
    val theme: String,
    val userToID: List<FioSearchElement> = listOf(),
    val typeID: Long = 2,
    val folderID: Any? = null,
    val messageImportant: Boolean = false,

    @get:JsonProperty("isSendDuplicateMessage")@field:JsonProperty("isSendDuplicateMessage")
    val isSendDuplicateMessage: Boolean? = null,

    val massMailingID: Any? = null,
    val selectedCourses: List<Any> = listOf(),
    val selectedFacultets: List<Any> = listOf(),
    val selectedKafs: List<Any> = listOf(),
    val selectedForm: List<Any> = listOf(),
    val selectedConditionsEducation: List<Any> = listOf(),
    val selectedLevelEducation: List<Any> = listOf(),
    val selectedNationality: List<Any> = listOf(),
    val year: String = "2023-2024",
    val dispatchDate: Any? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.ALWAYS)
        }
        fun fromJson(json: String) = mapper.readValue<SendMessageModel>(json)
    }
}
