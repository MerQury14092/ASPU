package com.merqury.aspu.services.mailbox.outbox.models

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.*



data class SendedMessageResponse (
    val data: Data? = null,
    val state: Long? = null,
    val msg: String? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        fun fromJson(json: String) = mapper.readValue<SendedMessageResponse>(json)
    }
    data class Data (
        val mailInfo: MailInfo? = null,
        val messageID: Long? = null
    )

    data class MailInfo (
        val htmlMessage: String? = null,
        val markdownMessage: String? = null,
        val message: String? = null,
        val theme: String? = null,
        val userToID: List<FioSearchElement>? = null,
        val typeID: Long? = null,
        val folderID: Any? = null,
        val dispatchDate: Any? = null,
        val messageImportant: Boolean? = null,

        @get:JsonProperty("isSendDuplicateMessage")@field:JsonProperty("isSendDuplicateMessage")
        val isSendDuplicateMessage: Boolean? = null,

        val parentID: Any? = null,
        val parentFamilyID: Any? = null,
        val massMailingID: Any? = null,
        val selectedCourses: List<Any?>? = null,
        val selectedFacultets: List<Any?>? = null,
        val selectedKafs: List<Any?>? = null,
        val selectedForm: List<Any?>? = null,
        val selectedConditionsEducation: List<Any?>? = null,
        val selectedLevelEducation: List<Any?>? = null,
        val selectedNationality: List<Any?>? = null,
        val year: String? = null
    )

}

