package com.merqury.aspu.ui.navfragments.timetable

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.services.getSearchResults
import com.merqury.aspu.ui.ModalWindow
import com.merqury.aspu.ui.navfragments.timetable.DTO.SearchResult

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun SelectIdModalWindowPreview() {
    Box(
        modifier = Modifier
            .background(Color.Red)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            SelectIdModalWindowContent(selectIdModalWindowVisibility = mutableStateOf(false))
        }
    }
}

@Composable
fun SelectIdModalWindow(
    selectIdModalWindowVisibility: MutableState<Boolean>
) {
    ModalWindow(
        onDismiss = {
            selectIdModalWindowVisibility.value = false
        }
    ) {
        SelectIdModalWindowContent(selectIdModalWindowVisibility = selectIdModalWindowVisibility)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectIdModalWindowContent(
    selectIdModalWindowVisibility: MutableState<Boolean>
) {
    Box(modifier = Modifier.fillMaxSize(.9f)) {
        val searchResults = remember {
            mutableStateOf(arrayListOf<SearchResult>())
        }
        val queryString = remember {
            mutableStateOf("")
        }
        SearchBar(
            query = queryString.value,
            onQueryChange = { queryChange ->
                queryString.value = queryChange
                getSearchResults(queryString.value, searchResults)
            },
            onSearch = {},
            active = true,
            onActiveChange = {
                             if(!it)
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
                    contentScale = ContentScale.Fit
                )
            },
        )
        {
            LazyColumn {
                items(count = searchResults.value.size) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .clickable {

                            selectedId.value = searchResults.value[it].name
                            selectedOwner.value = searchResults.value[it].owner
                            timetableLoaded.value = false
                            selectIdModalWindowVisibility.value = false

                        }) {
                        Column {
                            Text(
                                text = searchResults.value[it].name,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                textAlign = TextAlign.Center
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}