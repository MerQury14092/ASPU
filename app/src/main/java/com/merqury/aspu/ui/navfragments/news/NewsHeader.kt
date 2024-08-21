package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.canopas.lib.showcase.IntroShowcase
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.navfragments.news.NewsStates.pagerState
import com.merqury.aspu.ui.navfragments.news.NewsStates.selectedFaculty
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.training.TrainingStates
import com.merqury.aspu.ui.training.hintTargetModifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsHeader() {
    IntroShowcase(
        showIntroShowCase = TrainingStates.newsHeader,
        onShowCaseCompleted = {
            TrainingStates.newsHeader = false
            TrainingStates.news = true
        }) {
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
                        painter = painterResource(id = selectedFaculty.logo),
                        contentDescription = "",
                        modifier = Modifier
                            .size(60.dp)
                            .bounceClick {
                                showFacultySelectModalWindow {
                                    pagerState = PagerState { 1 }
                                    selectedFaculty = it
                                }
                            }
                            .then(
                                hintTargetModifier(
                                    0,
                                    "Выбор категории",
                                    "Кликнув сюда вы можете выбрать категорию"
                                )
                            ),
                        contentScale = ContentScale.Fit,
                    )
                }
                Column {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.3f)
                            .bounceClick {
                                showPageSelectModalWindow()
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .then(
                                    hintTargetModifier(
                                        1,
                                        "Выбор страницы",
                                        "Кликнув сюда, вы можете вручную выбрать нужную" +
                                                " вам страницу"
                                    )
                                ),
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1} из ${pagerState.pageCount}",
                                textAlign = TextAlign.Center,
                                color = SurfaceTheme.text.color
                            )
                        }
                    }
                }
            }
        }
    }
}