package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.merqury.aspu.R
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.getNews
import com.merqury.aspu.ui.GifImage
import org.json.JSONObject

var newsLoaded = mutableStateOf(false)
val currentPage = mutableIntStateOf(1)
val countPages = mutableIntStateOf(1)

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Box(Modifier.fillMaxSize()) {
        Header(mutableIntStateOf(1), 103, mutableStateOf(NewsCategoryEnum.IPIMiF))
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun NewsScreen() {
    val newsResponseState = mutableStateOf(JSONObject())
    val selectedFaculty = remember {
        mutableStateOf(NewsCategoryEnum.IRiIF)
    }
    getNews(selectedFaculty.value, currentPage.intValue, newsResponseState, countPages, newsLoaded)
    NewsContent(newsResponseState, selectedFaculty)
}

@Composable
fun NewsContent(
    data: MutableState<JSONObject>,
    selectedFaculty: MutableState<NewsCategoryEnum>
) {
    if (!newsLoaded.value) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(currentPage, countPages.intValue, selectedFaculty)
            GifImage(modifier = Modifier.fillMaxSize(), R.drawable.loading)
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
                        imageUrl = article.getString("previewImage")
                    )
                }
            }
        }
    }
}

