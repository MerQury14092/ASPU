package com.merqury.aspu.services.news

import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.services.api.news.GetNewsService
import com.merqury.aspu.services.api.news.models.FullArticle
import com.merqury.aspu.services.api.news.models.NewsResponse
import com.merqury.aspu.ui.async
import com.merqury.aspu.ui.printlog
import java.net.UnknownHostException

fun getNews(
    pageNumber: Int,
    selectedFaculty: NewsCategoryEnum,
    onError: (String) -> Unit,
    onSuccess: (NewsResponse) -> Unit
) {
    async {
        try {
            val newsService = GetNewsService.getInstance()
            val response = newsService.getArticlesByFaculty(selectedFaculty.name, pageNumber)
            onSuccess(response)
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                onError("Нет подключения к сети Интернет")
            } else {
                printlog("NEWS FETCH ERROR: $e")
                onError("Неизвестная ошибка")
            }
        }
    }
}

fun getNewsArticle(
    faculty: NewsCategoryEnum,
    id: Int,
    onSuccess: (FullArticle) -> Unit,
    onError: (String) -> Unit
) {
    async {
        try {
            val newsService = GetNewsService.getInstance()
            val response = newsService.getArticleById(faculty.name, id)
            onSuccess(response)
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                onError("Нет подключения к сети Интернет")
            } else {
                printlog("NEWS ARTICLE FETCH ERROR: $e")
                onError("Неизвестная ошибка")
            }
        }
    }
}


//@OptIn(ExperimentalFoundationApi::class)
//fun urlForCurrentFaculty(): String {
//    return when (selectedFaculty.value.name) {
//        "agpu" -> "agpu.net/news.php?PAGEN_1=${pagerState.value.currentPage}"
//        "educationaltechnopark" -> "www.agpu.net/struktura-vuza/educationaltechnopark/news/news.php?PAGEN_1=${pagerState.value.currentPage}"
//        "PedagogicalQuantorium" -> "www.agpu.net/struktura-vuza/PedagogicalQuantorium/news/news.php?PAGEN_1=${pagerState.value.currentPage}"
//        else -> "agpu.net/struktura-vuza/faculties/${selectedFaculty.value.name}/news/news.php?PAGEN_1=${pagerState.value.currentPage}"
//    }
//}