package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.news.getNews
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import org.json.JSONObject

val showArticleView = mutableStateOf(false)
val clickedArticleId = mutableIntStateOf(0)
val selectedFaculty = mutableStateOf(
    NewsCategoryEnum.valueOf(
        settingsPreferences.getString(
            "news_category",
            "agpu"
        )!!
    )
)


@SuppressLint("UnrememberedMutableState")
@Composable
fun NewsScreen(header: MutableState<@Composable () -> Unit>) {
    if (showArticleView.value)
        ArticleView()
    NewsContent(header)
}

@OptIn(ExperimentalFoundationApi::class)
internal val pagerState = mutableStateOf(PagerState { 1 })
private var newsLoaded by mutableStateOf(false)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun NewsContent(
    header: MutableState<@Composable () -> Unit>
) {

    header.value = {
        TitleHeader(title = "Новости")
        NewsHeader(selectedFaculty, newsLoaded)
    }
    if (!newsLoaded) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(SurfaceTheme.background.color)
        ) {
            NewsItemLoadingPlaceholder()
            NewsItemLoadingPlaceholder()
            NewsItemLoadingPlaceholder()
            NewsItemLoadingPlaceholder()
        }
        getNews(1, {}) { _, pageCount ->
            pagerState.value = PagerState { pageCount }
            newsLoaded = true
        }
    } else {
        HorizontalPager(
            state = pagerState.value,
            modifier = Modifier.background(SurfaceTheme.background.color)
        ) {
            NewsPage(pageNumber = it)
        }
    }

}

@Composable
private fun NewsPage(
    pageNumber: Int
) {
    var loaded by remember {
        mutableStateOf(false)
    }
    var json by remember {
        mutableStateOf<JSONObject?>(null)
    }
    if (!loaded) {
        getNews(
            pageNumber+1,
            {}
        ) { response, _ ->
            json = response
            loaded = true
        }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            NewsItemLoadingPlaceholder()
            NewsItemLoadingPlaceholder()
            NewsItemLoadingPlaceholder()
            NewsItemLoadingPlaceholder()
        }
    } else {
        LazyColumn(
            modifier = Modifier.background(SurfaceTheme.background.color)
        ) {
            items(count = json!!.getJSONArray("articles").length()) {
                val article = json!!.getJSONArray("articles").getJSONObject(it)
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

fun reloadNews(){
    newsLoaded = false
}
