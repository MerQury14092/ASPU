package com.merqury.aspu.ui.navfragments.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.merqury.aspu.services.exam.moodleCookie
import com.merqury.aspu.services.quiz.models.AnswerType
import com.merqury.aspu.services.quiz.models.QuestionModel
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun Question(
    questionModel: QuestionModel
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
        ThemeText(text = questionModel.qText)
        Divider(color = SurfaceTheme.divider.color)
        questionModel.images.forEach {
            printlog(it)
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .addHeader("Cookie", moodleCookie())
                    .build(),
                modifier = Modifier
                    .fillMaxWidth()
                    .zoomable(rememberZoomState()),
                onError = {
                    printlog("error: ${it.result.throwable.message}")
                },
                loading = {
                    CircularProgressIndicator()
                },
                contentDescription = "",
            )
        }
        questionModel.answers.forEach {
            Row {
                when (it.type) {
                    AnswerType.check -> {
                        var selected by remember {
                            mutableStateOf(false)
                        }
                        Checkbox(checked = selected, onCheckedChange = { selected = it })
                    }

                    AnswerType.radio -> {
                        var selected by remember {
                            mutableStateOf(false)
                        }
                        RadioButton(selected = selected, { selected = !selected })
                    }

                    AnswerType.text -> {
                        var text by remember {
                            mutableStateOf("")
                        }
                        TextField(value = text, onValueChange = { text = it })
                    }
                }
                ThemeText(text = it.text)
            }
        }
    }
}