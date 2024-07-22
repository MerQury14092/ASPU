package com.merqury.aspu.ui.greetings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.ui.theme.ThemeText

@Composable
fun UnofficialApplication() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.aspu), contentDescription = null)
            Spacer(modifier = Modifier.size(50.dp))
            ThemeText(
                text = "Неофициальное приложение",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.size(10.dp))
            ThemeText(
                text = "Данное приложение не является официальным так как АГПУ не давало " +
                        "согласия и каких либо привилегий разработчику. Из-за этого в " +
                        "приложении вы не найдёте функционала для преподавателя (ЭИОС, составление " +
                        "экзаменов и т.д.). Максимум - посмотреть расписание. Так же разработчику не был " +
                        "выдан доступ к базам данных АГПУ, поэтому все данные, которые вы видите в " +
                        "приложении преобразованы из веб-страниц. Это значит что могут быть какие-либо " +
                        "ошибки или при изменении структуры веб-страницы приложение может перестать работать"
            )
        }
    }
}