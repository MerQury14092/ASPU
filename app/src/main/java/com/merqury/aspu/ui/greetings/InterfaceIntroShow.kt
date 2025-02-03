package com.merqury.aspu.ui.greetings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.training.TrainingStates

@Composable
fun InterfaceIntroShow(onFinish: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.aspu), contentDescription = null)
            Spacer(modifier = Modifier.size(50.dp))
            if(AppSettings.timetableId != "timetable_id") {
                ThemeText(
                    text = "Обзор интерфейса",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.size(20.dp))
                ThemeText(
                    text = "Хотите ли вы, чтобы приложение показало и рассказало вам о функциональности " +
                            "некоторых кнопок в приложении?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .bounceClick {
                                onFinish()
                            }
                            .background(SurfaceTheme.button.color, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemeText(text = "Нет")
                    }
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .bounceClick {
                                TrainingStates.isTraining = true
                                TrainingStates.newsNavItem = true
                                onFinish()
                            }
                            .background(SurfaceTheme.button.color, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemeText(text = "Да")
                    }
                }
            } else {
                ThemeText(
                    text = "Заполните все данные!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.size(20.dp))
                ThemeText(
                    text = "Перед тем как зайти в приложение, пожалуйста, укажите кто вы или вашу группу",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}