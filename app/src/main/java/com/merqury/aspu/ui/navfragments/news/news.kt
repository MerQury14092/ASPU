package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.news.getNews
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.selected_page
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
    if (selectedNewsRoute)
        header.value = {
            TitleHeader(title = "Новости")
            NewsHeader(selectedFaculty, newsLoaded)
        }
    var errorString by remember {
        mutableStateOf<String?>(null)
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
        getNews(1, {
            errorString = it
            newsLoaded = true
        }) { _, pageCount ->
            pagerState.value = PagerState {
                pageCount
            }
            newsLoaded = true
        }
    } else {
        if (errorString != null)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceTheme.background.color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorString!!, color = SurfaceTheme.text.color)
            }
        else
            HorizontalPager(
                state = pagerState.value,
                modifier = Modifier.background(SurfaceTheme.background.color),
            ) {
                NewsPage(pageNumber = it)
            }
    }

}

private inline val selectedNewsRoute get() = selected_page.value == "news"


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
    var errorString by remember {
        mutableStateOf<String?>(null)
    }
    if (!loaded) {
        getNews(
            pageNumber + 1,
            {
                errorString = it
                loaded = true
            }
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
        if (errorString != null)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceTheme.background.color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorString!!, color = SurfaceTheme.text.color)
            }
        else
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

fun reloadNews() {
    newsLoaded = false
}
