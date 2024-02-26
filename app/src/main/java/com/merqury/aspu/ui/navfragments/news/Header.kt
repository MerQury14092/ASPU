package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.merqury.aspu.enums.NewsCategoryEnum


@Composable
fun Header(
    currentPage: MutableIntState,
    countPages: Int,
    selectedFaculty: MutableState<NewsCategoryEnum>
) {
    val facultySelectDialogVisible = remember {
        mutableStateOf(false)
    }
    val pageSelectDialogVisible = remember {
        mutableStateOf(false)
    }
    if (facultySelectDialogVisible.value) {
        FacultySelectModalWindow(
            facultySelectDialogVisible = facultySelectDialogVisible,
            selectedFaculty = selectedFaculty
        )
    }
    if (pageSelectDialogVisible.value) {
        PageSelectModalWindow(
            pageSelectDialogVisible = pageSelectDialogVisible
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .background(Color.Gray)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Button(onClick = {
                    if (newsLoaded.value)
                        facultySelectDialogVisible.value = true
                }) {
                    Text(text = selectedFaculty.value.localizedName)
                }
            }
            Column {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .clickable {
                            if (newsLoaded.value)
                                pageSelectDialogVisible.value = true
                        }
                ) {
                    Text(
                        text = "${currentPage.intValue} из $countPages",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column {
                Row {
                    Button(onClick = {
                        if (newsLoaded.value && currentPage.intValue > 1)
                            currentPage.intValue--
                    }) {
                        Text(text = "<")
                    }
                    Button(onClick = {
                        if (newsLoaded.value && currentPage.intValue < countPages)
                            currentPage.intValue++
                    }) {
                        Text(text = ">")
                    }
                }
            }
        }
    }
}