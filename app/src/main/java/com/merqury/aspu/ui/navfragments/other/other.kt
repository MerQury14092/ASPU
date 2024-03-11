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
import com.merqury.aspu.ui.showWebPage
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

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
            .background(theme.value[SurfaceTheme.background]!!)
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Нажмите на кнопку «АГПУ», чтоб посетить мобильную версию сайта",
                color = theme.value[SurfaceTheme.text]!!,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Divider(color = theme.value[SurfaceTheme.divider]!!)
            data class Entry(
                val name: String,
                val icon: Int,
                val url: String,
                val scheme: String = "http"
            )
            listOf(
                Entry(
                    "Сведения об образовательной организации",
                    R.drawable.info,
                    "www.agpu.net/sveden/index.php",
                ),
                Entry(
                    "История вуза",
                    R.drawable.history,
                    "www.agpu.net/about/history.php"
                ),
                Entry(
                    "Структура университета",
                    R.drawable.struct,
                    "www.agpu.net/struktura-vuza/index.php"
                ),
                Entry(
                    "Институты/Факультеты и кафедры",
                    R.drawable.faculty,
                    "www.agpu.net/struktura-vuza/faculties-institutes/index.php"
                ),
                Entry(
                    "Кампус и общежития",
                    R.drawable.campuses,
                    "www.agpu.net/studentu/obshchezhitiya/index.php"
                ),
                Entry(
                    "Календарь мероприятий",
                    R.drawable.calendar,
                    "www.agpu.net/meropriyatiya/index.php"
                ),
                Entry(
                    "Педагогический состав руководства",
                    R.drawable.pedagogs,
                    "www.agpu.net/sveden/employees/index.php"
                ),
                Entry(
                    "Учебный план",
                    R.drawable.study_plan,
                    "plany.agpu.net/Plans/"
                ),
                Entry(
                    "Аккаунт ЭИОС",
                    R.drawable.account,
                    "plany.agpu.net/WebApp/#/"
                ),
                Entry(
                    "Рабочие программы",
                    R.drawable.programs,
                    "plany.agpu.net/programmy/"
                ),
//                Entry("Методические материалы",
//                    R.drawable.book_alt,
//                    "plany.agpu.net/programmy/"
//                ),
                Entry(
                    "Часто задаваемые вопросы",
                    R.drawable.faq,
                    "www.agpu.net/contacts/FAQ.php"
                ),
                Entry(
                    "Контакты",
                    R.drawable.contacts,
                    "www.agpu.net/contacts/index.php"
                ),
                Entry(
                    "АГПУ во ВКонтакте",
                    R.drawable.vk,
                    "vk.com/agpu_official",
                    "vkontakte"
                ),
                Entry(
                    "АГПУ в Телеграм",
                    R.drawable.telegram,
                    "t.me/agpu_official",
                    "tg"
                ),
                Entry(
                    "АГПУ в YouTube",
                    R.drawable.youtube,
                    "www.youtube.com/channel/UCknLhL11-3y4jS7U0S8vGcw?view_as=subscriber",
                    "vnd.youtube"
                ),
            ).forEach {
                HyperReferenceButton(
                    name = it.name,
                    icon = it.icon,
                    url = it.url,
                    scheme = it.scheme
                )
            }
        }
    }
}


@Composable
fun HyperReferenceButton(
    name: String,
    icon: Int,
    url: String,
    scheme: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                showWebPage(url, scheme)
            },
        colors = CardDefaults.cardColors(
            containerColor = theme.value[SurfaceTheme.foreground]!!
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
                        colorFilter = ColorFilter.tint(theme.value[SurfaceTheme.text]!!)
                    )
                Text(
                    text = name,
                    fontSize = 16.sp,
                    color = theme.value[SurfaceTheme.text]!!,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}