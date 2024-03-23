package com.merqury.aspu.services.profile.models

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.*

val mapper = jacksonObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}

data class ProfileInfo (
    val data: Data? = null,
    val state: Long? = null,
    val msg: String? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        fun fromJson(json: String) = mapper.readValue<ProfileInfo>(json)
    }
}

data class Data (
    val studentID: Long? = null,
    val fullName: String? = null,
    val showZachBook: Boolean? = null,
    val domintoryNumber: String? = null,
    val numberRoom: Any? = null,
    val fullNameT: String? = null,
    val name: String? = null,
    val middleName: String? = null,
    val migrRegistrationAddressProduction: Any? = null,
    val migrRegistrationDateToMigration: String? = null,
    val migrRegistrationDateToStudyVisa: String? = null,
    val numRecordBook: String? = null,
    val numberMobile: String? = null,
    val surname: String? = null,
    val birthday: String? = null,
    val nationality: String? = null,
    val group: Group? = null,
    val email: String? = null,
    val login: String? = null,
    val emailForTeams: Any? = null,
    val admissionYear: String? = null,
    val lastEnterDate: String? = null,
    val course: String? = null,
    val faculty: String? = null,
    val plan: Plan? = null,
    val trainingDirection: String? = null,
    val photoLink: String? = null,
    val verPhoto: Any? = null,
    val activeSwapPhotoAndVerification: Boolean? = null,
    val activeMigrationRegistration: Boolean? = null,

    @get:JsonProperty("isMigrStud")@field:JsonProperty("isMigrStud")
    val isMigrStud: Boolean? = null,

    val scientificDirector: Any? = null,
    val showButtonChoiceDis: Boolean? = null,
    val allowChangePass: Boolean? = null,
    val showRaspButton: Boolean? = null,
    val linkRaspButton: Any? = null,
    val showGraphButton: Boolean? = null,
    val showVedButton: Boolean? = null,
    val showResultButton: Boolean? = null,

    @get:JsonProperty("isLocked")@field:JsonProperty("isLocked")
    val isLocked: Boolean? = null,

    @get:JsonProperty("isLockedVed")@field:JsonProperty("isLockedVed")
    val isLockedVed: Boolean? = null,

    val libraryСard: Any? = null,
    val online: Boolean? = null,
    val hideLinks: Boolean? = null,
    val message: String? = null,
    val htmlBlock: String? = null,
    val activeSwapPhoto: Boolean? = null,
    val byPassSheets: List<Any?>? = null,
    val status: Long? = null,
    val ratingActivation: Boolean? = null,
    val linkPsychology: Any? = null,
    val portfolioIncluded: Boolean? = null,
    val debtsGraphIncluded: Boolean? = null,
    val needDormitory: Boolean? = null,
    val vkID: Any? = null,
    val googleID: Any? = null,
    val yandexID: Any? = null,
    val allowChangePassStudent: Boolean? = null,
    val hidePlan: Boolean? = null,
    val hideMoveStory: Boolean? = null,
    val eliteEducationID: Any? = null,
    val scopusID: Any? = null,
    val kaf: Kaf? = null,
    val facul: Facul? = null
)

data class Facul (
    val faculID: Long? = null,
    val faculName: String? = null,
    val aud: String? = null,
    val phone: String? = null
)

data class Group (
    val item1: String? = null,
    val item2: Long? = null
)

data class Kaf (
    val kafID: Long? = null,
    val kafName: String? = null,
    val aud: String? = null,
    val phone: String? = null
)

data class Plan (
    val item1: String? = null,
    val item2: Long? = null,
    val item3: Boolean? = null
)

