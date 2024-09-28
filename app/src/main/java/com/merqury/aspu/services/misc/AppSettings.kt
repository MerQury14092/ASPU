package com.merqury.aspu.services.misc

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.merqury.aspu.appContext

private val settingsPreferences =
    appContext?.getSharedPreferences("settings", Context.MODE_PRIVATE)!!


@Suppress("UNCHECKED_CAST")
class PermanentMutableState<T>(val name: String, defaultValue: T) : MutableState<T> {
    private val state: MutableState<T> = when (defaultValue) {
        is String -> mutableStateOf(settingsPreferences.getString(name, defaultValue)!! as T)
        is Int -> mutableStateOf(settingsPreferences.getInt(name, defaultValue) as T)
        is Boolean -> mutableStateOf(settingsPreferences.getBoolean(name, defaultValue) as T)
        is Long -> mutableStateOf(settingsPreferences.getLong(name, defaultValue) as T)
        else -> throw RuntimeException()
    }

    override var value: T
        get() = state.value
        set(value) {
            when (value) {
                is String -> settingsPreferences.edit().putString(name, value).apply()
                is Int -> settingsPreferences.edit().putInt(name, value).apply()
                is Boolean -> settingsPreferences.edit().putBoolean(name, value).apply()
                is Long -> settingsPreferences.edit().putLong(name, value).apply()
                else -> throw RuntimeException()
            }
            state.value = value
        }

    override fun component1(): T = state.value

    override fun component2(): (T) -> Unit = {value = it}

}

class AppSettings {
    companion object {
        fun clear() {
            settingsPreferences.edit().clear().apply()
        }
        var whoIsUser by PermanentMutableState("user", "student")
        var initialRoute by PermanentMutableState("initial_route", "news")
        var newsCategory by PermanentMutableState("news_category", "agpu")
        var timetableId by PermanentMutableState("timetable_id", "ВМ-ИВТ-2-1")
        var timetableIdOwner by PermanentMutableState("timetable_id_owner", "GROUP")
        var timeCache by PermanentMutableState("timeCache", 0L)
        var timetableFiltration by PermanentMutableState("filtration_on", false)
        var debugMode by PermanentMutableState("debug_mode", false)
        var eiosLogged by PermanentMutableState("eios_logged", false)
        var useIncludedBrowser by PermanentMutableState("use_included_browser", false)
        var textInNavbar by PermanentMutableState("text_in_navbar", true)
        var selectedSubgroup by PermanentMutableState("selected_subgroup", 0)
        var selectedTheme by PermanentMutableState("theme", "light")
        var examFirstName by PermanentMutableState("exam-first-name", "Имя")
        var examLastName by PermanentMutableState("exam-last-name", "Фамилия")
        var firstLaunch by PermanentMutableState("first_launch", true)
        var colorTimetable by PermanentMutableState("color_timetable", true)
    }
    class Eios {
        companion object {
            var showBlockNames by PermanentMutableState("show_block_names", false)
            var showBlockNumber by PermanentMutableState("show_block_number", false)
            var showFullHours by PermanentMutableState("show_full_hours", false)
            var groupByBlocks by PermanentMutableState("group_by_blocks", true)
            var startSemester by PermanentMutableState("start_semester", true)
        }
    }
}