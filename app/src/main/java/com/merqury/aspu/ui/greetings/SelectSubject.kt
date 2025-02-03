package com.merqury.aspu.ui.greetings

import android.annotation.SuppressLint
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.R
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.services.profile.getProfileInfo
import com.merqury.aspu.services.timetable.getSearchResults
import com.merqury.aspu.services.timetable.models.SearchContent
import com.merqury.aspu.services.timetable.models.SearchContentElement
import com.merqury.aspu.ui.EditableText
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.navfragments.profile.profileInfo
import com.merqury.aspu.ui.navfragments.profile.secretPreferences
import com.merqury.aspu.ui.navfragments.profile.showEiosAuthModalWindow
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import kotlinx.coroutines.launch

@Composable
fun SelectSubject() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.aspu), contentDescription = null)
            Spacer(modifier = Modifier.size(50.dp))
            ThemeText(
                text = if (AppSettings.whoIsUser == "student") "В какой вы группе?"
                else "Ваши инициалы",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.size(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight(.4f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SelectSubjectList()
                if(AppSettings.whoIsUser == "student" && AppConfig.useEiosConfig().canUse)
                    EiosLogin()
                else
                    Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

fun hideKeyboard() {
    greetingsPagerActivity?.let { view ->
        val imm = view.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.window.decorView.rootView.windowToken, 0)
    }

}

private var eiosLoading by mutableStateOf(false)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EiosLogin() {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .bounceClick {
                showEiosAuthModalWindow {
                    eiosLoading = true
                    getProfileInfo(
                        secretPreferences.getString("authToken", null)!!,
                        secretPreferences.getInt("userId", 0),
                        onClosure = {
                            eiosLoading = false
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(4)
                            }
                        }
                    ) {
                        eiosLoading = false
                        printlog(it)
                        profileInfo = it
                        AppSettings.whoIsUser = "student"
                        AppSettings.timetableIdOwner = "GROUP"
                        AppSettings.timetableId = it.data!!.group!!.item1!!
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(4)
                        }
                    }
                }
                hideKeyboard()
            }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    SurfaceTheme.button.color,
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                ThemeText(text = "Или войдите через ЭИОС")
            }
            Image(
                painterResource(id = R.drawable.account),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    SurfaceTheme.text.color
                ),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
private fun SelectSubjectList() {
    Column(modifier = Modifier.fillMaxWidth()){
        var searchContent by remember {
            mutableStateOf<SearchContent?>(null)
        }
        var query by remember {
            mutableStateOf(if(AppSettings.timetableId == "timetable_id") "" else AppSettings.timetableId)
        }
        LaunchedEffect(eiosLoading) {
            if(AppSettings.eiosLogged && !eiosLoading){
                query = AppSettings.timetableId
            }
        }
        EditableText(
            value = query, onChange = {
                if (it.contains("\n")) {
                    hideKeyboard()
                    return@EditableText
                }
                query = it
                searchContent = null
            },
            enabled = !AppSettings.eiosLogged,
            placeholder = if (AppSettings.whoIsUser == "student") "Введите наименование вашей группы"
            else "Введите вашу фамилию",
            modifier = Modifier
                .background(
                    SurfaceTheme.foreground.color,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        )

        if(!AppSettings.eiosLogged) {
            Column {
                if (searchContent == null) {
                    getSearchResults(query, {}) {
                        searchContent = it
                    }
                    repeat(2) {
                        SearchContentElementPlaceholder()
                    }
                } else {
                    searchContent!!
                        .filter {
                            AppSettings.timetableIdOwner.lowercase() == it.type.lowercase()
                        }
                        .take(2)
                        .forEach {
                            SearchContentElement(it = it) { str ->
                                query = str
                            }
                        }
                }
            }
        }
    }
}


@Composable
fun SearchContentElement(it: SearchContentElement, onClickClosure: (String) -> Unit) {
    Box(
        modifier = Modifier
            .bounceClick {
                AppSettings.timetableId = it.searchContent
                onClickClosure(it.searchContent)
                hideKeyboard()
            }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    SurfaceTheme.button.color,
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                ThemeText(text = it.searchContent)
            }
            if (AppSettings.timetableId == it.searchContent) {
                Image(
                    Icons.Rounded.Check,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        SurfaceTheme.text.color
                    ),
                    modifier = Modifier.size(15.dp)
                )
            } else
                Spacer(modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
fun SearchContentElementPlaceholder() {
    Box(
        modifier = Modifier
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    SurfaceTheme.button.color,
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                ThemeText(
                    text = "ВМ ИВТ 2 1", modifier = Modifier.placeholder(
                        visible = true,
                        color = SurfaceTheme.placeholder_primary.color,
                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                        shape = RoundedCornerShape(15.dp)
                    )
                )
            }
            Spacer(modifier = Modifier.size(15.dp))
        }
    }
}