package com.merqury.aspu.ui.training

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TrainingCenter {
    companion object {
        var isTraining by mutableStateOf(false)

        var aspuButton by mutableStateOf(false)
        var aspuButtonDescription = ""
        var aspuButtonHintClosure = {}

        var newsNavItem by mutableStateOf(false)
        var newsHeader by mutableStateOf(false)
        var news by mutableStateOf(false)

        var timetableNavItem by mutableStateOf(false)
        var timetableHeader by mutableStateOf(false)
        var timetable by mutableStateOf(false)

        var otherNavItem by mutableStateOf(false)
        var other by mutableStateOf(false)

        var settingsNavItem by mutableStateOf(false)
        var settings by mutableStateOf(false)

        var accountNavItem by mutableStateOf(false)
        var accountHeader by mutableStateOf(false)
        var account by mutableStateOf(false)
    }
}