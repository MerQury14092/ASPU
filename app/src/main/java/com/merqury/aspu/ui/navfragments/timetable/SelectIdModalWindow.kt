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
import com.merqury.aspu.services.network.executeSqlQuery
import com.merqury.aspu.services.timetable.getFacultiesAndThemGroups
import com.merqury.aspu.services.timetable.getSearchResults
import com.merqury.aspu.services.timetable.toInitials
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.services.timetable.models.FacultiesList
import com.merqury.aspu.services.timetable.models.SearchContent
import com.merqury.aspu.services.timetable.models.SearchContentElement
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim

@SuppressLint("MutableCollectionMutableState")
val facultiesList = mutableStateOf(FacultiesList(listOf()))
fun getButtonsFacultyAndGroups(
    it: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
): HashMap<String, () -> Unit> {
    return HashMap<String, () -> Unit>().apply {
        facultiesList.value.forEach { faculty ->
            put(faculty.facultyName.toAbbreviation()) {
                showSelectListDialog(
                    sortedByAlphabet = true,
                    buttons = HashMap<String, () -> Unit>().apply {
                        faculty.groups.forEach { group ->
                            put(group) {
                                onResultClick(SearchContentElement(group, "Group", 0, 0))
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
    showSimpleModalWindow(containerColor = SurfaceTheme.background.colorWithoutAnim) {
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
                        containerColor = SurfaceTheme.foreground.color
                    )
                ) {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { textFieldValue.value = it },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = SurfaceTheme.text.color,
                            cursorColor = SurfaceTheme.text.color,
                            placeholderColor = SurfaceTheme.disable.color,
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
                                colorFilter = ColorFilter.tint(SurfaceTheme.disable.color)
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Введите наименование",
                                color = SurfaceTheme.disable.color
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Divider(color = SurfaceTheme.divider.color)
                val buttonsMapState = remember {
                    mutableStateOf(mapOf("Загружается..." to {}))
                }
                Column {
                    if (facultiesLoaded.value)
                        buttonsMapState.value = getButtonsFacultyAndGroups(it, onResultClick)
                    if (
                        textFieldValue.value.isEmpty()
                    ) {
                        if (filteredBy.lowercase() == "any" || filteredBy.lowercase() == "group")
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
                                        containerColor = SurfaceTheme.foreground.color
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Image(
                                                painter = painterResource(id = R.drawable.group),
                                                contentDescription = null,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.size(25.dp),
                                                colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                                            )
                                            Text(
                                                "Выбрать из списка групп",
                                                color = SurfaceTheme.text.color,
                                                modifier = Modifier.padding(start = 10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        if (filteredBy.lowercase() == "any" || filteredBy.lowercase() == "teacher")
                            Column {
                                Card(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable {
                                            showSelectDepartmentForTeacherWindow(it, onResultClick)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = SurfaceTheme.foreground.color
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Image(
                                                painter = painterResource(id = R.drawable.teacher),
                                                contentDescription = null,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.size(30.dp),
                                                colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                                            )
                                            Text(
                                                "Выбрать из списка учителей",
                                                color = SurfaceTheme.text.color,
                                                modifier = Modifier.padding(start = 10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        if (filteredBy.lowercase() == "any" || filteredBy.lowercase() == "classroom")
                            Column {
                                Card(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable {
                                            showSelectCorpsForAudiencesWindow(it, onResultClick)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = SurfaceTheme.foreground.color
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.audience),
                                                contentDescription = null,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.size(25.dp),
                                                colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                                            )
                                            Text(
                                                "Выбрать из списка аудиторий",
                                                color = SurfaceTheme.text.color,
                                                modifier = Modifier.padding(start = 10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                    }
                    if (selectedId.value != settingsPreferences.getString(
                            "timetable_id",
                            "ВМ-ИВТ-2-1"
                        ) && filteredBy.lowercase() == "any" && textFieldValue.value.isEmpty()
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
                                containerColor = SurfaceTheme.foreground.color
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.back),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(25.dp),
                                        colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                                    )
                                    Text(
                                        "Вернуть ваше расписание",
                                        color = SurfaceTheme.text.color,
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
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
                            containerColor = SurfaceTheme.foreground.color
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(it.searchContent, color = SurfaceTheme.text.color)
                        }
                    }
                }
            }
        }
    }
}

fun showSelectDepartmentForTeacherWindow(
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    val buttons = mutableStateOf(mapOf(
        "Загружается..." to {}
    ))
    loadDepartmentsOnButtons(buttons, selectIdWindowVisibility, onResultClick)
    showSelectListDialog(buttons, sortedByAlphabet = true)
}

fun loadDepartmentsOnButtons(
    buttons: MutableState<Map<String, () -> Unit>>,
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    executeSqlQuery(
        """
            SELECT * FROM departments
        """.trimIndent(),
        {
            val buttonEntries = HashMap<String, () -> Unit>()
            while (it.next()) {
                val departmentId = it.getInt("id")
                buttonEntries.put(
                    it.getString("name")
                ) {
                    showSelectTeacherWindow(departmentId, selectIdWindowVisibility, onResultClick)
                }
            }
            buttons.value = buttonEntries
        },
        {
            buttons.value = mapOf(
                "Прозошла ошибка" to {}
            )
        }
    )
}

fun showSelectTeacherWindow(
    departmentId: Int,
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    val buttons = mutableStateOf(mapOf(
        "Загружается..." to {}
    ))
    loadTeachersOnButtons(buttons, departmentId, selectIdWindowVisibility, onResultClick)
    showSelectListDialog(buttons, sortedByAlphabet = true)
}

fun loadTeachersOnButtons(
    buttons: MutableState<Map<String, () -> Unit>>,
    departmentId: Int,
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    executeSqlQuery(
        """
            SELECT * FROM teachers t 
            JOIN lnk_teacher_department ltd ON t.id = ltd.teacher_id 
            WHERE ltd.department_id = $departmentId
        """.trimIndent(),
        {
            val buttonEntries = HashMap<String, () -> Unit>()
            while (it.next()) {
                val fio = "${it.getString("last_name")} " +
                        "${it.getString("first_name")} " +
                        it.getString("father_name")
                buttonEntries.put(
                    fio
                ) {
                    onResultClick(
                        SearchContentElement(
                            fio.toInitials(),
                            "Teacher",
                            0,
                            0
                        )
                    )
                    selectIdWindowVisibility.value = false
                }
            }
            buttons.value = buttonEntries
        },
        {
            buttons.value = mapOf(
                "Прозошла ошибка" to {}
            )
        }
    )
}

fun showSelectCorpsForAudiencesWindow(
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    val buttons = mutableStateOf(mapOf(
        "Загружается..." to {}
    ))
    loadCorpsOnButtons(buttons, selectIdWindowVisibility, onResultClick)
    showSelectListDialog(buttons, sortedByAlphabet = true)
}

fun loadCorpsOnButtons(
    buttons: MutableState<Map<String, () -> Unit>>,
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    executeSqlQuery(
        """
            SELECT * FROM corps
        """.trimIndent(),
        {
            val buttonEntries = HashMap<String, () -> Unit>()
            while (it.next()) {
                val corpsId = it.getInt("id")
                buttonEntries[it.getString("name")] = {
                    showSelectAudienceWindow(corpsId, selectIdWindowVisibility, onResultClick)
                }
            }
            buttons.value = buttonEntries
        },
        {
            buttons.value = mapOf(
                "Прозошла ошибка" to {}
            )
        }
    )
}

fun showSelectAudienceWindow(
    corpsId: Int,
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    val buttons = mutableStateOf(mapOf(
        "Загружается..." to {}
    ))
    loadAudiencesOnButtons(buttons, corpsId, selectIdWindowVisibility, onResultClick)
    showSelectListDialog(buttons, sortedByAlphabet = false)
}

fun loadAudiencesOnButtons(
    buttons: MutableState<Map<String, () -> Unit>>,
    corpsId: Int,
    selectIdWindowVisibility: MutableState<Boolean>,
    onResultClick: (searchResult: SearchContentElement) -> Unit
) {
    executeSqlQuery(
        """
            SELECT * FROM audiences a 
            JOIN corps c ON a.corps_id = c.id 
            WHERE c.id = $corpsId
        """.trimIndent(),
        {
            val buttonEntries = HashMap<String, () -> Unit>()
            while (it.next()) {
                val audienceId = it.getString("name")
                buttonEntries[audienceId] = {
                    onResultClick(
                        SearchContentElement(
                            audienceId,
                            "Classroom",
                            0,
                            0
                        )
                    )
                    selectIdWindowVisibility.value = false
                }
            }
            buttons.value = buttonEntries.toSortedMap { o1, o2 ->
                return@toSortedMap if (o1.extractDigits().toInt() > o2.extractDigits().toInt())
                    1
                else -1
            }
        },
        {
            buttons.value = mapOf(
                "Прозошла ошибка" to {}
            )
        }
    )
}

fun String.extractDigits(): String {
    val builder = StringBuilder()
    forEach {
        if (it.isDigit())
            builder.append(it)
    }
    return builder.toString()
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