package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.getNews
import com.merqury.aspu.ui.SwipeableBox
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import org.json.JSONObject

var newsLoaded = mutableStateOf(false)
val currentPage = mutableIntStateOf(1)
val countPages = mutableIntStateOf(1)
val showArticleView = mutableStateOf(false)
val clickedArticleId = mutableIntStateOf(0)
val newsLoadSuccess = mutableStateOf(true)
val selectedFaculty = mutableStateOf(
    NewsCategoryEnum.valueOf(
        settingsPreferences.getString(
            "news_category",
            "agpu"
        )!!
    )
)
val newsResponseState = mutableStateOf(JSONObject())


@SuppressLint("UnrememberedMutableState")
@Composable
fun NewsScreen(header: MutableState<@Composable () -> Unit>) {
    if (showArticleView.value)
        ArticleView()
    NewsContent(newsResponseState, selectedFaculty, header)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsContent(
    data: MutableState<JSONObject>,
    selectedFaculty: MutableState<NewsCategoryEnum>,
    header: MutableState<@Composable () -> Unit>
) {
    header.value = {
        TitleHeader(title = "Новости")
        NewsHeader(currentPage, countPages.intValue, selectedFaculty)
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = !newsLoaded.value,
        onRefresh = {
            newsLoaded.value = false
        }
    )
    Column(modifier = Modifier.fillMaxSize()) {

        if (!newsLoaded.value) {
            getNews(
                selectedFaculty.value,
                currentPage.intValue,
                newsResponseState,
                countPages,
                newsLoaded,
                newsLoadSuccess
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = theme.value[SurfaceTheme.background]!!)
        ) {
            if (newsLoaded.value) {
                if (!newsLoadSuccess.value)
                    Box(
                        modifier = Modifier
                            .pullRefresh(pullRefreshState)
                            .fillMaxSize()
                            .background(theme.value[SurfaceTheme.background]!!)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Ошибка загрузки новостей!",color = theme.value[SurfaceTheme.text]!!)
                    }
                else
                    SwipeableBox(
                        onSwipeRight = {
                            currentPage.intValue++
                            reloadNews()
                        },
                        onSwipeLeft = {
                            currentPage.intValue--
                            reloadNews()
                        },
                        swipeableRight = currentPage.intValue < countPages.intValue,
                        swipeableLeft = currentPage.intValue > 1
                    ) {
                        LazyColumn (
                            modifier = Modifier.background(theme.value[SurfaceTheme.background]!!)
                        ){
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
            }
            PullRefreshIndicator(
                refreshing = !newsLoaded.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = theme.value[SurfaceTheme.foreground]!!,
                contentColor = theme.value[SurfaceTheme.text]!!
            )
        }
    }
}

fun reloadNews() {
    newsLoaded.value = false
}
