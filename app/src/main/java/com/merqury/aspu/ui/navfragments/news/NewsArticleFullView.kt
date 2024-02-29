package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import com.merqury.aspu.R
import com.merqury.aspu.services.getNewsArticle
import com.merqury.aspu.ui.GifImage
import com.merqury.aspu.ui.ModalWindow
import com.merqury.aspu.ui.showSimpleModalWindow
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
        }
    ) {
        val articleLoaded = remember {
            mutableStateOf(false)
        }
        val articleJson = remember {
            mutableStateOf(JSONObject())
        }
        if (!articleLoaded.value) {
            getNewsArticle(
                selectedFaculty.value,
                clickedArticleId.intValue,
                articleJson,
                articleLoaded
            )
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GifImage(
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit,
                    gifResourceId =  R.drawable.loading,
                )
            }
        } else {
            ArticleViewContent(articleJson.value)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArticleViewContent(articleJson: JSONObject) {
    Box(modifier = Modifier.padding(15.dp)) {
        Divider()
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = articleJson.getString("title"),
                fontSize = 25.sp,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = articleJson.getString("date"),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Divider()
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontSize = 15.sp,
                text = articleJson.getString("description")
            )
            Divider()
            val images = articleJson.getJSONArray("images")

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceAround
            ){
                for (i in 0..<images.length()) {
                    SubcomposeAsyncImage(
                        model = images.get(i).toString().replace("test", "www"),
                        contentDescription = "url",
                        loading = {
                            Box (
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ){
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