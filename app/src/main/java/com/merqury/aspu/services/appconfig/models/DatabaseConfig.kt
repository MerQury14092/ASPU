package com.merqury.aspu.services.appconfig.models


import com.fasterxml.jackson.annotation.JsonProperty


data class DatabaseConfig (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val javaDriverName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val jdbcUrl: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val dbmsName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val host: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val port: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val user: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val pass: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val ssl: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val tables: Tables
)

data class Tables (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val teacherTable: TeacherTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val departmentTable: DepartmentTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val corpsTable: CorpsTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val audienceTable: AudienceTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val facultyTable: FacultyTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val corpsImageTable: CorpsImageTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val teacherDepartmentLinkTable: TeacherDepartmentLinkTable,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val departmentFacultyLinkTable: DepartmentFacultyLinkTable
)

data class AudienceTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: AudienceTableFields
)

data class AudienceTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val corpsId: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val audienceName: String
)

data class CorpsImageTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: CorpsImageTableFields
)

data class CorpsImageTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val corpsId: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val imageUrl: String
)

data class CorpsTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: CorpsTableFields
)

data class CorpsTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val id: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val corpsName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val latitude: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val longitude: String
)

data class DepartmentFacultyLinkTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: DepartmentFacultyLinkTableFields
)

data class DepartmentFacultyLinkTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val faculty: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val department: String
)

data class DepartmentTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: DepartmentTableFields
)

data class DepartmentTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val id: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val departmentName: String
)

data class FacultyTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: FacultyTableFields
)

data class FacultyTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val id: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val facultyName: String
)

data class TeacherDepartmentLinkTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: TeacherDepartmentLinkTableFields
)

data class TeacherDepartmentLinkTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val teacher: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val department: String
)

data class TeacherTable (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val name: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val exists: Boolean,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fields: TeacherTableFields
)

data class TeacherTableFields (
    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val id: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val lastName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val firstName: String,

    @get:JsonProperty(required=true)@field:JsonProperty(required=true)
    val fatherName: String
)
