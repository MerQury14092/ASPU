package com.merqury.aspu.services.mailbox.inbox.models

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.*



data class Inbox (
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
        fun fromJson(json: String) = mapper.readValue<Inbox>(json)
    }
    data class Data (
        val messages: List<MessageElement>? = null,
        val totalPages: Long? = null,
        val page: Long? = null,
        val hiddenNextPage: Boolean? = null,
        val faculties: List<Faculty>? = null,
        val showParent: Boolean? = null
    )
}



data class Faculty (
    val facultyID: Long? = null,
    val facultyName: String? = null,
    val facultyShortName: String? = null,

    @get:JsonProperty("isDelete")@field:JsonProperty("isDelete")
    val isDelete: Boolean? = null,

    val dekan: String? = null,
    val signature3: Any? = null,
    val site: Boolean? = null,
    val typeStudy: Long? = null,
    val certMinNumber: Any? = null,
    val certEnabled: Any? = null,
    val certInProcessing: Any? = null,
    val certStudInProcessing: Any? = null,
    val teacherDekanID: Any? = null,
    val certIndex: Any? = null,
    val aud: String? = null,
    val phoneNumber: String? = null,
    val cipher: String? = null,
    val number: Long? = null,
    val journalsPrivateMode: Any? = null,
    val educationSpaceID: Any? = null
)

data class MessageElement (
    val id: Long? = null,
    val messageID: Long? = null,
    val folderID: Any? = null,
    val recipientID: Long? = null,
    val recipientsCount: Long? = null,
    val photoLinkRecipientID: String? = null,
    val photoLinkUserID: Any? = null,

    @get:JsonProperty("isDelete")@field:JsonProperty("isDelete")
    var isDelete: Long? = null,

    var dateRead: String? = null,
    var starMessage: Boolean? = null,

    @get:JsonProperty("userIdFromMessage")@field:JsonProperty("userIdFromMessage")
    val userIDFromMessage: String? = null,

    @get:JsonProperty("userIdGroupFromMessage")@field:JsonProperty("userIdGroupFromMessage")
    val userIDGroupFromMessage: String? = null,

    @get:JsonProperty("userIdGroupToMessage")@field:JsonProperty("userIdGroupToMessage")
    val userIDGroupToMessage: String? = null,

    @get:JsonProperty("userIdToMessage")@field:JsonProperty("userIdToMessage")
    val userIDToMessage: String? = null,

    val emailUserID: String? = null,
    val emailRecipientID: String? = null,
    val message: MessageMessage? = null,
    val files: List<File>? = null
)

data class File (
    val attachmentID: Long? = null,
    val messageID: Long? = null,
    val fileName: String? = null,
    val path: String? = null,
    val size: Long? = null,
    val typeFile: String? = null,
    val userID: Long? = null,
    val sessionID: Any? = null,

    @get:JsonProperty("isDelete")@field:JsonProperty("isDelete")
    val isDelete: Any? = null,

    val deletedUserID: Any? = null
)

data class MessageMessage (
    val messageID: Long? = null,
    val userID: Long? = null,
    val typeID: Long? = null,
    val parentID: Any? = null,
    val parentFamilyID: Any? = null,
    val theme: String? = null,
    val htmlMessage: String? = null,
    val markdownMessage: String? = null,
    val message: String? = null,
    val dispatchDate: String? = null,
    val attachment: Any? = null,
    val messageImportant: Boolean? = null,

    @get:JsonProperty("isDelete")@field:JsonProperty("isDelete")
    val isDelete: Long? = null,

    val disciplineID: Any? = null,
    val files: Any? = null,
    val type: Type? = null,
    val user: Any? = null
)

data class Type (
    val typeID: Long? = null,
    val type: String? = null,
    val description: String? = null,
    val comment: String? = null,
    val npp: Any? = null,
    val hidden: Any? = null
)


