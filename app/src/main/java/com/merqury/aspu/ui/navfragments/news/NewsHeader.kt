package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
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
import com.merqury.aspu.enums.NewsCategoryEnum

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun NewsHeaderPreview() {
    Box(Modifier.fillMaxSize()) {
        NewsHeader(mutableIntStateOf(1), 103, mutableStateOf(NewsCategoryEnum.ipimif))
    }
}

@Composable
fun NewsHeader(
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

            Column(
                modifier = Modifier.fillMaxWidth(.3f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = selectedFaculty.value.logo),
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            if (newsLoaded.value)
                                facultySelectDialogVisible.value = true
                        },
                    contentScale = ContentScale.Fit,
                )
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
                    Column (
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "${currentPage.intValue} из $countPages",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Column {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.left_arrow),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 15.dp)
                            .clickable {
                                if (newsLoaded.value && currentPage.intValue > 1) {
                                    currentPage.intValue--
                                    reloadNews()
                                }
                            }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.right_arrow),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 15.dp)
                            .clickable {
                                if (newsLoaded.value && currentPage.intValue < countPages) {
                                    currentPage.intValue++
                                    reloadNews()
                                }
                            }
                    )
                }
            }
        }
    }
}