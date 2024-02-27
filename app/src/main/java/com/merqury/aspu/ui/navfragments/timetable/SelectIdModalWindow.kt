package com.merqury.aspu.ui.navfragments.timetable

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.ui.ModalWindow

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun SelectIdModalWindowPreview(){
    SelectIdModalWindowContent(selectIdModalWindowVisibility = mutableStateOf(false))
}

@Composable
fun SelectIdModalWindow(
    selectIdModalWindowVisibility: MutableState<Boolean>
){
    ModalWindow (
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
){
    Box (modifier = Modifier.fillMaxSize(.7f)){
        val queryString = remember {
            mutableStateOf("")
        }
        SearchBar(
            query = queryString.value,
            onQueryChange = {queryChange ->
                            queryString.value = queryChange
            },
            onSearch = {
                selectIdModalWindowVisibility.value = false
            },
            active = true,
            onActiveChange = {},
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
        ) {

        }
    }
}