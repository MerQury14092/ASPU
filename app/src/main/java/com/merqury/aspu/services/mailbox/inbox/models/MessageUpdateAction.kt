package com.merqury.aspu.services.mailbox.inbox.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


data class MessageUpdateAction (
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

enum class ActionType(val id: Long){
    Trash(1),
    Delete(2)
}