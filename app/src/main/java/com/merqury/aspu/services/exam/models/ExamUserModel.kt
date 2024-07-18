package com.merqury.aspu.services.exam.models

import java.time.LocalDateTime

data class ExamUserModel(
    val firstName: String,
    val lastName: String,
    var photoUrl: String?,
    var email: String?,
    var country: String?,
    var city: String?,
    var lastLogin: LocalDateTime?,
    var courses: List<ExamCourse>
)
