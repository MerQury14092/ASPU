package com.merqury.aspu.ui.navfragments.timetable

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.services.getFacultiesAndThemGroups
import com.merqury.aspu.services.getSearchResults
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.timetable.DTO.FacultiesList
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchContent
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchContentElement
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

@SuppressLint("MutableCollectionMutableState")
val facultiesList = mutableStateOf(FacultiesList(listOf()))
fun getButtonsFacultyAndGroups(
    it: MutableState<Boolean>
): HashMap<String, () -> Unit> {
    return HashMap<String, () -> Unit>().apply {
        facultiesList.value.forEach { faculty ->
            put(faculty.facultyName.toAbbreviation()) {
                showSelectListDialog(
                    sortedByAlphabet = true,
                    buttons = HashMap<String, () -> Unit>().apply {
                    faculty.groups.forEach { group ->
                        put(group) {
                            selectedId.value = group
                            selectedOwner.value = "GROUP"
                            it.value = false
                            timetableLoaded.value = false
                        }
                    }
                })
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
fun showSelectIdModalWindow(
    filteredBy: String = "any",
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    showSimpleModalWindow(containerColor = theme.value[SurfaceTheme.background]!!) {
        val thisWindowVisibility = it
        Box(modifier = Modifier.padding(10.dp)) {
            Column {
                val textFieldValue = remember {
                    mutableStateOf("")
                }
                val searchResults = remember {
                    mutableStateOf(SearchContent(listOf()))
                }
                val successSearchResults = remember {
                    mutableStateOf(true)
                }
                getSearchResults(
                    textFieldValue.value,
                    searchResults,
                    successSearchResults
                )
                val facultiesLoaded = remember {
                    mutableStateOf(false)
                }
                Card(
                    modifier = Modifier.padding(10.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = theme.value[SurfaceTheme.foreground]!!
                    )
                ) {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { textFieldValue.value = it },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = theme.value[SurfaceTheme.text]!!,
                            cursorColor = theme.value[SurfaceTheme.text]!!,
                            placeholderColor = theme.value[SurfaceTheme.disable]!!,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.search_icon),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(35.dp),
                                colorFilter = ColorFilter.tint(theme.value[SurfaceTheme.disable]!!)
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Введите наименование",
                                color = theme.value[SurfaceTheme.disable]!!
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Divider(color = theme.value[SurfaceTheme.divider]!!)
                val buttonsMapState = remember {
                    mutableStateOf(mapOf("Загружается..." to {}))
                }
                Column {
                    if (facultiesLoaded.value)
                        buttonsMapState.value = getButtonsFacultyAndGroups(it)
                    if (
                        textFieldValue.value.isEmpty()
                        && (filteredBy.lowercase() == "any" || filteredBy.lowercase() == "group")
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        getFacultiesAndThemGroups(
                                            facultiesList,
                                            facultiesLoaded,
                                            mutableStateOf(true)
                                        )
                                        showSelectListDialog(
                                            sortedByAlphabet = true,
                                            buttons = buttonsMapState
                                        )
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = theme.value[SurfaceTheme.foreground]!!
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Или выберите из списка групп",
                                        color = theme.value[SurfaceTheme.text]!!
                                    )
                                }
                            }
                        }
                    }
                    if (selectedId.value != settingsPreferences.getString(
                            "timetable_id",
                            "ВМ-ИВТ-2-1"
                        ) && filteredBy.lowercase() == "any"
                    )
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    selectedOwner.value = if (settingsPreferences.getString(
                                            "user",
                                            "student"
                                        ) == "student"
                                    )
                                        "GROUP"
                                    else
                                        "TEACHER"
                                    selectedId.value =
                                        settingsPreferences.getString(
                                            "timetable_id",
                                            "ВМ-ИВТ-2-1"
                                        )!!
                                    timetableLoaded.value = false
                                    it.value = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = theme.value[SurfaceTheme.foreground]!!
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Вернуть ваше расписание",
                                    color = theme.value[SurfaceTheme.text]!!
                                )
                            }
                        }
                }
                val results = if (filteredBy.lowercase() == "any")
                    searchResults.value
                else
                    searchResults.value.filter { it.type.lowercase() == filteredBy.lowercase() }
                results.forEach {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                onResultClick(it)
                                thisWindowVisibility.value = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = theme.value[SurfaceTheme.foreground]!!
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(it.searchContent, color = theme.value[SurfaceTheme.text]!!)
                        }
                    }
                }
            }
        }
    }
}

fun String.toAbbreviation(): String {
    if (this.lowercase() == "аспирантура")
        return this
    val sntns = this.split("(")
    val words = sntns[0]
        .trim()
        .replace("-", " ")
        .replace(",", "")
        .split(" ")

    var res = ""
    words.forEach {
        if (it.lowercase() == "исторический")
            res += "Ист"
        else
            res += if (it.length > 1)
                it[0].uppercase()
            else
                it
    }

    (1..<sntns.size).forEach {
        res += " (" + sntns[it]
    }
    return res.replace("форма обучения", "ФО")
}