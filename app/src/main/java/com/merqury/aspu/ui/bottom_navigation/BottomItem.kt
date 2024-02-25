package com.merqury.aspu.ui.bottom_navigation

import com.merqury.aspu.R

sealed class BottomItem (val title: String, val iconId: Int, val route: String){
    data object News: BottomItem("News", R.drawable.news_icon, "news")
    data object Timetable: BottomItem("Timetable", R.drawable.timetable_icon, "timetable")
    data object Other: BottomItem("Other", R.drawable.other_icon, "other")
    data object Settings: BottomItem("Settings", R.drawable.settings_icon, "settings")
}