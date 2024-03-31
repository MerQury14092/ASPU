package com.merqury.aspu.services.mailbox.inbox.models

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.*



data class MessageAction (
    val actionTypeID: Long? = null,
    val selectedFolderID: Any? = null,
    val listMail: List<ListMail>? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<MessageAction>(json)
    }
}

data class ListMail (
    val folderID: Any? = null,
    val messageID: Long? = null,
    val message: MessageMessage? = null,

    @get:JsonProperty("isDelete")
    @field:JsonProperty("isDelete")
    val isDelete: Long? = null,

    val recipientID: Long? = null,
    val userID: Long? = null
)



data class MessageState (
    val dateRead: String? = null,
    val id: Long? = null,

    @get:JsonProperty("isDelete")@field:JsonProperty("isDelete")
    val isDelete: Long? = null,

    val messageID: Long? = null,
    val folderID: Any? = null,
    val recipientID: Long? = null,
    val starMessage: Any? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
        fun fromJson(json: String) = mapper.readValue<MessageState>(json)
    }
}
