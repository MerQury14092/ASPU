package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.services.getNewsArticle
import com.merqury.aspu.ui.ModalWindow
import com.merqury.aspu.ui.navfragments.timetable.prettyDate
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.json.JSONObject

@Composable
fun ArticleView() {
    ModalWindow(
        modifier = Modifier
            .fillMaxSize(.95f),
        onDismiss = {
            showArticleView.value = false
        },
        background = theme.value[SurfaceTheme.background]!!
    ) {
        val articleLoaded = remember {
            mutableStateOf(false)
        }
        val articleJson = remember {
            mutableStateOf(JSONObject())
        }
        val newsArticleLoadSuccess = remember {
            mutableStateOf(true)
        }
        if (!articleLoaded.value) {
            getNewsArticle(
                selectedFaculty.value,
                clickedArticleId.intValue,
                articleJson,
                articleLoaded,
                newsArticleLoadSuccess
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color = theme.value[SurfaceTheme.background]!!),
                contentAlignment = Alignment.Center
            )
            {
                ArticleViewContentLoadingPlaceholder()
            }
        } else {
            if (newsArticleLoadSuccess.value)
                ArticleViewContent(articleJson.value)
            else
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = theme.value[SurfaceTheme.background]!!),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Ошибка загрузки статьи!", color = theme.value[SurfaceTheme.text]!!)
                }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArticleViewContent(articleJson: JSONObject) {
    Box(
        modifier = Modifier
            .padding(15.dp)
            .background(color = theme.value[SurfaceTheme.background]!!)
    ) {
        Divider(color = theme.value[SurfaceTheme.divider]!!)
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = articleJson.getString("title"),
                fontSize = 25.sp,
                fontStyle = FontStyle.Italic,
                color = theme.value[SurfaceTheme.text]!!
            )
            Text(
                text = prettyDate(articleJson.getString("date")),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = theme.value[SurfaceTheme.text]!!
            )
            Divider(color = theme.value[SurfaceTheme.divider]!!)
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontSize = 15.sp,
                text = articleJson.getString("description"),
                color = theme.value[SurfaceTheme.text]!!
            )
            Divider(color = theme.value[SurfaceTheme.divider]!!)
            val images = articleJson.getJSONArray("images")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 0..<images.length()) {
                    SubcomposeAsyncImage(
                        model = images.get(i).toString().replace("test", "www"),
                        contentDescription = "url",
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LinearProgressIndicator()
                            }
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .clickable {
                                showSimpleModalWindow(
                                    containerColor = Color.Transparent
                                ) {
                                    val pagerState = rememberPagerState(
                                        initialPage = i,
                                        pageCount = { images.length() })
                                    HorizontalPager(
                                        state = pagerState
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight(.5f)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            SubcomposeAsyncImage(
                                                model = images
                                                    .get(pagerState.currentPage)
                                                    .toString()
                                                    .replace("test", "www"),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .zoomable(rememberZoomState())
                                                    .fillMaxSize(),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleViewContentLoadingPlaceholder() {
    Box(
        modifier = Modifier
            .padding(15.dp)
            .background(color = theme.value[SurfaceTheme.background]!!)
    ) {
        Divider(color = theme.value[SurfaceTheme.divider]!!)
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .placeholder(
                            visible = true,
                            color = theme.value[SurfaceTheme.placeholder_primary]!!,
                            highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    textAlign = TextAlign.Center,
                    text = "Это очень крутой заголовок",
                    fontSize = 25.sp,
                    fontStyle = FontStyle.Italic,
                    color = theme.value[SurfaceTheme.text]!!
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Это дата",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .placeholder(
                            visible = true,
                            color = theme.value[SurfaceTheme.placeholder_primary]!!,
                            highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    textAlign = TextAlign.End,
                    color = theme.value[SurfaceTheme.text]!!
                )
            }
            Divider(color = theme.value[SurfaceTheme.divider]!!)
            Spacer(Modifier.size(10.dp))
            (1..50).forEach { _ ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                        .placeholder(
                            visible = true,
                            color = theme.value[SurfaceTheme.placeholder_primary]!!,
                            highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    textAlign = TextAlign.Left,
                    fontSize = 15.sp,
                    text = "Очень крутое описание",
                    color = theme.value[SurfaceTheme.text]!!
                )
            }
        }
    }
}