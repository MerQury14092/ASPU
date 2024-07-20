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
import androidx.compose.ui.text.style.TextAlign
import com.canopas.lib.showcase.IntroShowcase
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.news.getNews
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.selected_page
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.training.TrainingCenter
import com.merqury.aspu.ui.training.hintTargetModifier
import org.json.JSONObject

val showArticleView = mutableStateOf(false)
val clickedArticleId = mutableIntStateOf(0)
val selectedFaculty = mutableStateOf(
    NewsCategoryEnum.valueOf(AppSettings.newsCategory)
)
private var loaded by mutableStateOf(false)


@SuppressLint("UnrememberedMutableState")
@Composable
fun NewsScreen(header: MutableState<@Composable () -> Unit>) {
    if (TrainingCenter.news) {
        val onCompleted = {
            TrainingCenter.aspuButtonDescription = "Короткое нажатие откроет данную страницу " +
                    "новостей через браузер. Длинное нажатие обновит вкладку в приложении"
            TrainingCenter.aspuButtonHintClosure = {
                TrainingCenter.timetableNavItem = true
            }
            TrainingCenter.news = false
            TrainingCenter.aspuButton = true
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
                        "Для листания страниц используются свайпы"
                    )
                )
            }
        }
    }
    if (showArticleView.value)
        ArticleView()
    val useConfig = AppConfig.useNewsPageConfig()
    if (useConfig.canUse)
        NewsContent(header)
    else
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceTheme.background.color),
            contentAlignment = Alignment.Center
        ) {
            header.value = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TitleHeader(title = "Новости")
                }
            }
            ThemeText(
                text = useConfig.reason ?: "Новости пока не работают в данной версии",
                textAlign = TextAlign.Center
            )
        }
}

@OptIn(ExperimentalFoundationApi::class)
internal val pagerState = mutableStateOf(PagerState { 1 })

private var loading = false
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsContent(
    header: MutableState<@Composable () -> Unit>
) {

    val headerContent = @Composable {
        TitleHeader(title = "Новости")
        NewsHeader(selectedFaculty, loaded)
    }
    if (header.value != headerContent)
        header.value = headerContent
    var errorString by remember {
        mutableStateOf<String?>(null)
    }
    if (!loaded) {
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
        if(!loading){
            loading = true
            getNews(1, {
                errorString = it
                loaded = true
                loading = false
            }) { _, pageCount ->
                pagerState.value = PagerState {
                    pageCount
                }
                loaded = true
                loading = false
            }
        }
    } else if (loaded) {
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
                outOfBoundsPageCount = 1
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
