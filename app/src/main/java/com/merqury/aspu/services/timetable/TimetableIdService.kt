package com.merqury.aspu.services.timetable

import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.timetable.models.FacultiesList
import com.merqury.aspu.services.timetable.models.FacultiesListElement
import com.merqury.aspu.services.timetable.models.SearchContent
import com.merqury.aspu.ui.async
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.util.Objects
import java.util.Scanner


fun getSearchResults(
    query: String,
    searchResults: MutableState<SearchContent>,
    success: MutableState<Boolean>
) {
    getSearchResults(
        checkFirstLetter(query),
        {
            success.value = false
        }
    ) {
        success.value = true
        searchResults.value = it
    }
}

/* на сайте в названиях групп, где первая буква должна значит Bachelors
* она почему то русская, но при входе в аккаунт ЭИОС, название
* устанавливается правильно, данный метод вызывается перед каждым запросом на
* расписание, чтобы исправить правильное название на то, что нужно it-institut*/
fun checkFirstLetter(query: String): String {
    if (Regex(".*-\\d+-\\d+").matches(query)) {
        return when (query[0]) {
            'B' -> "В${query.substring(1)}"
            'M' -> "М${query.substring(1)}"
            'A' -> "А${query.substring(1)}"
            'D' -> "Д${query.substring(1)}"
            else -> query
        }
    }
    return query
}

fun getSearchResults(
    query: String,
    onError: (String) -> Unit,
    onSuccess: (SearchContent) -> Unit
) {
    val url = "https://it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=${checkFirstLetter(query)}"
    val request = StringRequest(
        Request.Method.GET,
        url,
        {
            async {
                val res = SearchContent.fromJson(it)
                res.forEach {
                    it.searchContent = it.searchContent.split(",")[0]
                }
                onSuccess(res)
            }
        },
        {
            onError(it.message ?: it.javaClass.name)
            Log.d("network-error", "ERROR")
        }
    )
    requestQueue!!.add(request)
}

fun getSearchId(query: String, onLoaded: (resultId: Long, resultType: String) -> Unit) {
    async {
        val resp = URI(
            "https://it-institut.ru/SearchString/KeySearch?Id=118&SearchProductName=${
                URLEncoder.encode(
                    checkFirstLetter(query),
                    "utf-8"
                )
            }"
        ).toURL().readText()

        val type = resp
            .split("Type\":\"")[1]
            .split("\"")[0]

        val searchId = resp
            .split("SearchId\":")[1]
            .split(",")[0]
            .toLong()

        onLoaded(searchId, type)
    }
}


fun getFacultiesAndThemGroups(
    result: MutableState<FacultiesList>,
    loaded: MutableState<Boolean>,
    success: MutableState<Boolean>
) {
    async {
        try {
            val connection: HttpURLConnection =
                URL("http://it-institut.ru/SearchString/Index/118").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val sc = Scanner(Objects.requireNonNull(connection.inputStream))

            val builder = StringBuilder()

            while (sc.hasNextLine()) {
                builder.append(sc.nextLine()).append("\n")
            }


            val res = FacultiesList(
                Jsoup.parse(builder.toString()).getElementsByClass("card").map {
                    parseCardElement(it)
                }.toList()
            )
            result.value = res
            loaded.value = true
            success.value = true
        } catch (e: Exception) {
            loaded.value = true
            success.value = false
        }
    }
}

private fun parseCardElement(el: Element): FacultiesListElement {
    val facName = el.getElementsByTag("button").first().text()

    val list = el.getElementsByClass("p-2").map {
        it.getAllElements().first().text()
    }.toList()
    return FacultiesListElement(facName, list)
}