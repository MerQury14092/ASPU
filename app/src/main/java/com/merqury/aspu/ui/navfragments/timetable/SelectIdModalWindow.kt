package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.services.getSearchResults
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchResult
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

@OptIn(ExperimentalMaterial3Api::class)
fun showSelectIdModalWindow(
    filteredBy: String = "any",
    onResultClick: (searchResult: SearchResult) -> Unit
) {
    showSimpleModalWindow(containerColor = theme.value[SurfaceTheme.background]!!) {
        val selectIdModalWindowVisibility = it
        Box(modifier = Modifier.fillMaxSize(.9f)) {
            val searchResults = remember {
                mutableStateOf(arrayListOf<SearchResult>())
            }
            val queryString = remember {
                mutableStateOf("")
            }
            val resultsLoadSuccess = remember {
                mutableStateOf(true)
            }
            SearchBar(
                query = queryString.value,
                onQueryChange = { queryChange ->
                    queryString.value = queryChange
                    getSearchResults(queryString.value, searchResults, resultsLoadSuccess)
                },
                onSearch = {
                    var filteredList: List<SearchResult> = searchResults.value
                    if (filteredBy != "any") {
                        filteredList = searchResults.value.filter {
                            it.owner.lowercase() == filteredBy
                        }.toList()
                    }
                    if (searchResults.value.size > 0) {
                        onResultClick(filteredList[0])
                    }
                    selectIdModalWindowVisibility.value = false
                },
                active = true,
                onActiveChange = {
                    if (!it)
                        selectIdModalWindowVisibility.value = false
                },
                placeholder = {
                    Text("Введите название группы, имя преподавателя или аудиторию")
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "",
                        modifier = Modifier.size(35.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(theme.value[SurfaceTheme.text]!!)
                    )
                },
                colors = SearchBarDefaults.colors(
                    inputFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = theme.value[SurfaceTheme.text]!!
                    ),
                    containerColor = theme.value[SurfaceTheme.background]!!,
                    dividerColor = theme.value[SurfaceTheme.divider]!!,
                )
            )
            {
                var filteredList: List<SearchResult> = searchResults.value
                if (filteredBy != "any") {
                    filteredList = searchResults.value.filter {
                        it.owner.lowercase() == filteredBy
                    }.toList()
                }
                LazyColumn {
                    items(count = filteredList.size) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clickable {

                                onResultClick(filteredList[it])
                                selectIdModalWindowVisibility.value = false

                            }) {
                            Column {
                                Text(
                                    text = filteredList[it].name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    textAlign = TextAlign.Center,
                                    color = theme.value[SurfaceTheme.text]!!
                                )
                                Divider(color = theme.value[SurfaceTheme.divider]!!)
                            }
                        }
                    }
                }
            }
        }
    }
}