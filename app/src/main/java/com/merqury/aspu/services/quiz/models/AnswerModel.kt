package com.merqury.aspu.services.quiz.models

data class QuestionModel(
    val qText: String,
    val sequenceId: String,
    val sequenceValue: String,
    val images: List<String>,
    val answers: List<AnswerModel>
)

data class AnswerModel(
    val text: String,
    val type: AnswerType,
    val inputId: String,
    val inputValue: String
)

enum class AnswerType {
    radio,
    check,
    text
}