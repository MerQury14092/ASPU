package com.merqury.aspu.ui.navfragments.other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.showWebPage
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

@Composable
fun OtherScreen(header: MutableState<@Composable () -> Unit>) {
    header.value = { TitleHeader(title = when (settingsPreferences.getString("user", "student")) {
        "student" -> "Студенту"
        "teacher" -> "Педагогу"
        else -> "Кому?"
    }) }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(theme.value[SurfaceTheme.background]!!)){
        Column {
            mapOf(
                "Сведения об образовательной организации" to "http://www.agpu.net/sveden/index.php",
                "Календарь мероприятий" to "http://www.agpu.net/meropriyatiya/index.php",
                "Часто задаваемые вопросы" to "http://www.agpu.net/contacts/FAQ.php",
                "Контакты" to "http://www.agpu.net/contacts/index.php",
                "Руководство.  Педагогический (научно-педагогический состав)" to "http://www.agpu.net/sveden/employees/index.php",
                "Структура университета" to "http://www.agpu.net/struktura-vuza/index.php",
                "История вуза" to "http://www.agpu.net/about/history.php",
                "Учебный план" to "http://plany.agpu.net/Plans/",
                "Рабочие программы" to "http://plany.agpu.net/programmy/"

            ).entries.forEach {
                HyperReferenceButton(
                    name = it.key,
                    url = it.value
                )
            }
        }
    }

}



@Composable
fun HyperReferenceButton(
    name: String,
    url: String
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                showWebPage(url)
            },
        colors = CardDefaults.cardColors(
            containerColor = theme.value[SurfaceTheme.foreground]!!
        )
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Text(text = name, fontSize = 16.sp, color = theme.value[SurfaceTheme.text]!!)
        }
    }
}