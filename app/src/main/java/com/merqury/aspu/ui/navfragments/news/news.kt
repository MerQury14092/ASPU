package com.merqury.aspu.ui.navfragments.news

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.canopas.lib.showcase.IntroShowcase
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.api.news.models.NewsResponse
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.news.getNews
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.UiState
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.training.TrainingStates
import com.merqury.aspu.ui.training.hintTargetModifier

val showArticleView = mutableStateOf(false)
val clickedArticleId = mutableIntStateOf(0)


object NewsStates {
    var selectedFaculty by mutableStateOf(
        NewsCategoryEnum.valueOf(AppSettings.newsCategory)
    )

    @OptIn(ExperimentalFoundationApi::class)
    var pagerState by mutableStateOf(PagerState { 1 })
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun NewsScreen(header: MutableState<@Composable () -> Unit>) {
    if (TrainingStates.news) {
        val onCompleted = {
            TrainingStates.aspuButtonDescription = "Открывает данную страницу " +
                    "новостей через браузер"
            TrainingStates.aspuButtonHintClosure = {
                TrainingStates.timetableNavItem = true
            }
            TrainingStates.news = false
            TrainingStates.aspuButton = true
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            IntroShowcase(
                showIntroShowCase = true,
                onShowCaseCompleted = onCompleted,
                dismissOnClickOutside = true
            ) {
                Box(
                    modifier = hintTargetModifier(
                        0,
                        "Навигация по экрану",
                        "Свайп вправо - следующая страница. Свайп влево - предыдущая страница"
                    )
                )
            }
        }
    }
    if (showArticleView.value)
        ArticleView()
    val useConfig = AppConfig.useNewsConfig()
    if (useConfig.canUse)
        NewsContent(header)
    else
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceTheme.background.color),
            contentAlignment = Alignment.Center
        ) {
            val headerContent = @Composable {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TitleHeader(title = "Новости")
                }
            }
            if (header.value != headerContent)
                header.value = headerContent
            ThemeText(
                text = useConfig.reason ?: ("Новости были отключены разработчиком по " +
                        "неизвестной причине"),
                textAlign = TextAlign.Center
            )
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsContent(
    header: MutableState<@Composable () -> Unit>
) {
    val headerContent = @Composable {
        TitleHeader(title = "Новости")
        NewsHeader()
    }
    if (header.value != headerContent)
        header.value = headerContent

    HorizontalPager(state = NewsStates.pagerState) { page ->
        Box(modifier = Modifier.fillMaxSize()){
            NewsPage(page = page)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsPage(page: Int) {
    var loadedFaculty by remember {
        mutableStateOf(NewsStates.selectedFaculty.name.lowercase())
    }
    var newsResponse by remember {
        mutableStateOf<NewsResponse?>(null)
    }
    var errorString by remember {
        mutableStateOf<String?>(null)
    }
    var uiState by remember { mutableStateOf(UiState.IDLE)}
    LaunchedEffect(NewsStates.selectedFaculty) {
        if (loadedFaculty != NewsStates.selectedFaculty.name.lowercase()) {
            uiState = UiState.IDLE
            newsResponse = null
            errorString = null
        }
    }
    when(uiState) {
        UiState.IDLE -> {
            LaunchedEffect(uiState) {
                uiState = UiState.LOADING
                getNews(page+1, selectedFaculty = NewsStates.selectedFaculty, {
                    uiState = UiState.LOADED
                    errorString = it
                }){
                    uiState = UiState.LOADED
                    newsResponse = it
                    loadedFaculty = it.category.lowercase()
                }
            }
        }
        UiState.LOADING -> {
            Column (Modifier.verticalScroll(rememberScrollState())){
                NewsItemLoadingPlaceholder()
                NewsItemLoadingPlaceholder()
                NewsItemLoadingPlaceholder()
                NewsItemLoadingPlaceholder()
            }
        }
        UiState.LOADED -> {
            if(errorString != null) {
                NewsStates.pagerState = PagerState { 1 }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    ThemeText(text = errorString!!)
                }
            }
            else if(loadedFaculty.lowercase() == NewsStates.selectedFaculty.name.lowercase()){
                if(NewsStates.pagerState.pageCount != newsResponse!!.countPages)
                    NewsStates.pagerState = PagerState { newsResponse!!.countPages }
                LazyColumn {
                    items(newsResponse!!.articles) {
                        NewsItem(
                            title = it.title,
                            date = it.date,
                            imageUrl = it.previewImage,
                            id = it.id
                        )
                    }
                }
            }
        }
    }
}

