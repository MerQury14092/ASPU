package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchContentElement
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

fun showSelectIdModalWindow(
    filteredBy: String = "any",
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    showSimpleModalWindow(containerColor = theme.value[SurfaceTheme.background]!!) {
        Text(
            "not implemented yet",
            modifier = Modifier.padding(10.dp),
            color = theme.value[SurfaceTheme.text]!!
        )
    }
}