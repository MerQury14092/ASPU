package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import com.merqury.aspu.ui.bounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.navfragments.news.NewsStates.pagerState
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
fun showFacultySelectModalWindow(
    onSelectFaculty: (selectedFaculty: NewsCategoryEnum) -> Unit
) {
    showSimpleModalWindow (
        Modifier.fillMaxWidth(.8f),
        containerColor = SurfaceTheme.background.colorWithoutAnim
    ) {
        val coroutineScope = rememberCoroutineScope()
        NewsCategoryEnum.entries.forEach { entry ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .bounceClick {
                        onSelectFaculty(entry)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                        it.value = false
                    }
            ) {
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceTheme.foreground.color
                    )
                ){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Row(
                            modifier = Modifier.padding(5.dp)
                        ){
                            Image(
                                painter = painterResource(id = entry.logo),
                                contentDescription = "",
                                modifier = Modifier.size(35.dp),
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                text = entry.localizedName,
                                textAlign = TextAlign.Center,
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp),
                                color = SurfaceTheme.text.color
                            )
                            Image(
                                painter = painterResource(id = entry.logo),
                                contentDescription = "",
                                modifier = Modifier.size(35.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun showPageSelectModalWindow() {
    showSimpleModalWindow(
        Modifier.fillMaxWidth(.8f),
        containerColor = SurfaceTheme.background.colorWithoutAnim
    ) { pageSelectDialogVisible ->
        val coroutineScope = rememberCoroutineScope()
        LazyColumn {
            items(count = pagerState.pageCount) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .bounceClick {
                            coroutineScope.launch {
                                pagerState.scrollToPage(it)
                            }
                            pageSelectDialogVisible.value = false
                        }
                ) {
                    Text(
                        text = (it + 1).toString(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        color = SurfaceTheme.text.color
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = SurfaceTheme.divider.color)
            }
        }
    }
}