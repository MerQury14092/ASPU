package com.merqury.aspu.services.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import com.merqury.aspu.services.api.timetable.WeekIdService
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.navfragments.timetable.TimetableStates
import com.merqury.aspu.ui.navfragments.timetable.getDateByPage
import com.merqury.aspu.ui.openInBrowser
import com.merqury.aspu.ui.showWebPage

@OptIn(ExperimentalFoundationApi::class)
fun showTimetableWebPageView() {
    getSearchId(TimetableStates.timetableId) { id, type ->
        val url = "it-institut.ru/Raspisanie/SearchedRaspisanie?" +
                "OwnerId=118&" +
                "SearchId=$id&" +
                "SearchString=${TimetableStates.timetableId}&" +
                "Type=$type&" +
                "WeekId=${WeekIdService.weekIdByDate(getDateByPage(TimetableStates.pagerState.currentPage))}"

        if(AppSettings.useIncludedBrowser)
            showWebPage(url, "https")
        else
            openInBrowser(url, "https")
    }
}