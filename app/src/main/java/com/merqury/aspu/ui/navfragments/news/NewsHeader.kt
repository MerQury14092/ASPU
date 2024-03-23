package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color

@Composable
fun NewsHeader(
    currentPage: MutableIntState,
    countPages: Int,
    selectedFaculty: MutableState<NewsCategoryEnum>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                                showFacultySelectModalWindow{
                                    selectedFaculty.value = it
                                }
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
                                showPageSelectModalWindow()
                        }
                ) {
                    Column (
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "${currentPage.intValue} из $countPages",
                            textAlign = TextAlign.Center,
                            color = SurfaceTheme.text.color
                        )
                    }
                }
            }
        }
    }
}