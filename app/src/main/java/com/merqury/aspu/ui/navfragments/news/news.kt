package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merqury.aspu.R
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.getNews
import com.merqury.aspu.ui.GifImage
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

@Composable
fun NewsContent(
    data: MutableState<JSONObject>,
    selectedFaculty: MutableState<NewsCategoryEnum>
) {
    if (!newsLoaded.value) {
        getNews(
            selectedFaculty.value,
            currentPage.intValue,
            newsResponseState,
            countPages,
            newsLoaded
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Header(currentPage, countPages.intValue, selectedFaculty)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GifImage(
                    modifier = Modifier.size(50.dp),
                    gifResourceId = R.drawable.loading,
                    contentScale = ContentScale.Fit
                )
            }
        }
    } else {
        Column {
            Header(currentPage, data.value.getInt("countPages"), selectedFaculty)
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
    }
}

fun reloadNews() {
    newsLoaded.value = false
}
