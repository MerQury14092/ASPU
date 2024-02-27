package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.getNews
import org.json.JSONObject

var newsLoaded = mutableStateOf(false)
val currentPage = mutableIntStateOf(1)
val countPages = mutableIntStateOf(1)
val showArticleView = mutableStateOf(false)
val clickedArticleId = mutableIntStateOf(0)
val selectedFaculty = mutableStateOf(NewsCategoryEnum.agpu)
val newsResponseState = mutableStateOf(JSONObject())


@SuppressLint("UnrememberedMutableState")
@Composable
fun NewsScreen() {
    if (showArticleView.value)
        ArticleView()
    NewsContent(newsResponseState, selectedFaculty)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsContent(
    data: MutableState<JSONObject>,
    selectedFaculty: MutableState<NewsCategoryEnum>
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = !newsLoaded.value,
        onRefresh = {
            newsLoaded.value=false
        }
    )
    Column(modifier = Modifier.fillMaxSize()) {
        NewsHeader(currentPage, countPages.intValue, selectedFaculty)
        if (!newsLoaded.value) {
            getNews(
                selectedFaculty.value,
                currentPage.intValue,
                newsResponseState,
                countPages,
                newsLoaded
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ){
            if(newsLoaded.value){
                LazyColumn {
                    items(count = data.value.getJSONArray("articles").length()) {
                        val article = data.value.getJSONArray("articles").getJSONObject(it)
                        NewsItem(
                            title = article.getString("title"),
                            date = article.getString("date"),
                            imageUrl = article.getString("previewImage"),
                            id = article.getInt("id")
                        )
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = !newsLoaded.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Color.White
            )
        }
    }
}

fun reloadNews() {
    newsLoaded.value = false
}
