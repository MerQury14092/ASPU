package com.merqury.aspu.services.file.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class FileModel (
    val fileName: String,
    val size: Long,
    @JsonIgnore
    var content: ByteArray? = null
) {
    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.ALWAYS)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        fun fromJson(json: String) = mapper.readValue<FileModel>(json)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileModel

        if (fileName != other.fileName) return false
        if (size != other.size) return false
        return content.contentEquals(other.content)
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}