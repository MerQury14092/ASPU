package com.merqury.aspu.ui.navfragments.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.openInBrowser
import com.merqury.aspu.ui.showWebPage
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color

@Preview
@Composable
fun OtherPreview() {
    OtherScreenContent()
}

@Composable
fun OtherScreen(header: MutableState<@Composable () -> Unit>) {
    header.value = {
        TitleHeader(
            title = when (settingsPreferences.getString("user", "student")) {
                "student" -> "Студенту"
                "teacher" -> "Педагогу"
                else -> "Кому?"
            }
        )
    }
    OtherScreenContent()
}

@Composable
fun OtherScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTheme.background.color)
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Нажмите на кнопку «АГПУ», чтоб посетить мобильную версию сайта",
                color = SurfaceTheme.text.color,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Divider(color = SurfaceTheme.divider.color)
            data class WebEntry(
                val name: String,
                val icon: Int,
                val url: String,
                val scheme: String = "http",
                val inBrowser: Boolean = false
            )
            data class ActionEntry(
                val name: String,
                val icon: Int,
                val action: () -> Unit
            )
            listOf(
                WebEntry(
                    "Сведения об образовательной организации",
                    R.drawable.info,
                    "www.agpu.net/sveden/index.php",
                ),
                WebEntry(
                    "История вуза",
                    R.drawable.history,
                    "www.agpu.net/about/history.php"
                ),
                WebEntry(
                    "Структура университета",
                    R.drawable.struct,
                    "www.agpu.net/struktura-vuza/index.php"
                ),
                WebEntry(
                    "Институты/Факультеты и кафедры",
                    R.drawable.faculty,
                    "www.agpu.net/struktura-vuza/faculties-institutes/index.php"
                ),
                WebEntry(
                    "Кампус и общежития",
                    R.drawable.campuses,
                    "www.agpu.net/studentu/obshchezhitiya/index.php"
                ),
                WebEntry(
                    "Календарь мероприятий",
                    R.drawable.calendar,
                    "www.agpu.net/meropriyatiya/index.php"
                ),
                WebEntry(
                    "Педагогический состав руководства",
                    R.drawable.pedagogs,
                    "www.agpu.net/sveden/employees/index.php"
                ),
                WebEntry(
                    "Учебный план",
                    R.drawable.study_plan,
                    "plany.agpu.net/Plans/"
                ),
//                ActionEntry(
//                    "Аккаунт ЭИОС",
//                    R.drawable.account
//                ) {
//                  showEiosAuthModalWindow {
//                      routeTo("account")
//                  }
//                },
                WebEntry(
                    "Рабочие программы",
                    R.drawable.programs,
                    "plany.agpu.net/programmy/"
                ),
//                Entry("Методические материалы",
//                    R.drawable.book_alt,
//                    "plany.agpu.net/programmy/"
//                ),
                WebEntry(
                    "Часто задаваемые вопросы",
                    R.drawable.faq,
                    "www.agpu.net/contacts/FAQ.php"
                ),
                WebEntry(
                    "Контакты",
                    R.drawable.contacts,
                    "www.agpu.net/contacts/index.php"
                ),
                WebEntry(
                    "АГПУ во ВКонтакте",
                    R.drawable.vk,
                    "vk.com/agpu_official",
                    "vkontakte"
                ),
                WebEntry(
                    "АГПУ в Телеграм",
                    R.drawable.telegram,
                    "t.me/agpu_official",
                    inBrowser = true
                ),
                WebEntry(
                    "АГПУ в YouTube",
                    R.drawable.youtube,
                    "www.youtube.com/channel/UCknLhL11-3y4jS7U0S8vGcw?view_as=subscriber",
                    "vnd.youtube"
                ),
            ).forEach {
                if(it is WebEntry){
                    ActionButton(
                        name = it.name,
                        icon = it.icon
                    ) {
                        if (!it.inBrowser)
                            if (settingsPreferences.getBoolean("use_included_browser", false))
                                showWebPage(it.url, it.scheme)
                            else
                                openInBrowser(it.url, it.scheme)
                        else
                            openInBrowser(it.url, it.scheme)
                    }
                } /*else if (it is ActionEntry){
                    ActionButton(name = it.name, icon = it.icon, action = it.action)
                }*/
            }
        }
    }
}


@Composable
fun ActionButton(
    name: String,
    icon: Int,
    action: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                action()
            },
        colors = CardDefaults.cardColors(
            containerColor = SurfaceTheme.foreground.color
        )
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != 0)
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                    )
                Text(
                    text = name,
                    fontSize = 16.sp,
                    color = SurfaceTheme.text.color,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}