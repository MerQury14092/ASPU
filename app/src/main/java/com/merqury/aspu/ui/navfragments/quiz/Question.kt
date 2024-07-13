package com.merqury.aspu.ui.navfragments.quiz

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.services.exam.moodleCookie
import com.merqury.aspu.services.quiz.models.AnswerType
import com.merqury.aspu.services.quiz.models.QuestionModel
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Question(
    questionModel: QuestionModel,
    questionsAnswers: Map<Int, MutableMap<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceTheme.foreground.color, RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        if (questionModel.images.isNotEmpty())
            questionModel.images.first().let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .addHeader("Cookie", moodleCookie())
                            .build(),
                        onError = { error ->
                            printlog("error: ${error.result.throwable.message}")
                        },
                        modifier = Modifier.clickable {
                            showSimpleModalWindow(
                                containerColor = Color.Transparent
                            ) {
                                val pagerState = rememberPagerState(
                                    initialPage = 0,
                                    pageCount = { questionModel.images.size })
                                HorizontalPager(
                                    state = pagerState
                                ) { page ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight(.5f)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        SubcomposeAsyncImage(
                                            model = questionModel.images[page]
                                                .replace("test", "www"),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .zoomable(rememberZoomState())
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        },
                        contentDescription = "",
                    )
                }
            }
        ThemeText(
            text = questionModel.qText,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(10.dp))
        Divider(color = SurfaceTheme.divider.color)
        Spacer(modifier = Modifier.size(20.dp))
        var selectedRadio by remember {
            mutableIntStateOf(-1)
        }
        questionModel.answers.forEach { answer ->
            var selected by remember {
                mutableStateOf(false)
            }
            if (answer.type == AnswerType.select) {
                ThemeText(text = answer.text)
                var expanded by remember {
                    mutableStateOf(false)
                }
                var selectedText by remember {
                    mutableStateOf(
                        if (questionsAnswers[questionModel.questionNumber]!![answer.inputId] != null) {
                            answer.selectOptions!!.entries.first {
                                it.value == questionsAnswers[questionModel.questionNumber]!![answer.inputId]
                            }.key
                        } else "Выберите"
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = SurfaceTheme.text.color,
                            unfocusedTextColor = SurfaceTheme.text.color,
                            cursorColor = SurfaceTheme.text.color,
                            focusedPlaceholderColor = SurfaceTheme.disable.color,
                            unfocusedPlaceholderColor = SurfaceTheme.disable.color,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            containerColor = SurfaceTheme.button.color,
                            disabledTextColor = SurfaceTheme.disable.color
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        modifier = Modifier
                            .background(
                                SurfaceTheme.foreground.color,
                            )
                            .border(
                                2.dp, SurfaceTheme.disable.color,
                            ),
                        onDismissRequest = { expanded = false }
                    ) {
                        answer.selectOptions!!.forEach { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                            {
                                DropdownMenuItem(
                                    text = { ThemeText(text = option.key) },
                                    modifier = Modifier
                                        .background(
                                            SurfaceTheme.button.color,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(10.dp),
                                    colors = MenuDefaults.itemColors(
                                        textColor = SurfaceTheme.text.color,
                                    ),
                                    onClick = {
                                        selectedText = option.key
                                        expanded = false
                                        questionsAnswers[questionModel.questionNumber]!![answer.inputId] =
                                            option.value
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(5.dp))
                return@forEach
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (answer.type) {
                            AnswerType.radio -> {
                                selectedRadio = answer.inputValue!!.toInt()
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId] =
                                    answer.inputValue
                            }

                            AnswerType.check -> {
                                selected = !selected
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId] =
                                    if (selected) "1" else "0"
                            }

                            else -> {}
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (answer.type) {
                    AnswerType.check -> {
                        if (questionsAnswers[questionModel.questionNumber]!!.containsKey(answer.inputId)) {
                            selected =
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId] == "1"
                        }
                        Checkbox(
                            checked = selected,
                            onCheckedChange = { change ->
                                selected = change
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId] =
                                    if (change) "1" else "0"
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SurfaceTheme.text.color,
                                checkmarkColor = SurfaceTheme.button.color,
                                uncheckedColor = SurfaceTheme.text.color
                            )
                        )
                    }

                    AnswerType.radio -> {
                        if (questionsAnswers[questionModel.questionNumber]!!.containsKey(answer.inputId)) {
                            selectedRadio =
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId]!!.toInt()
                        }
                        RadioButton(
                            selected = answer.inputValue!!.toInt() == selectedRadio,
                            {
                                selectedRadio = answer.inputValue.toInt()
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId] =
                                    answer.inputValue
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = SurfaceTheme.text.color,
                                unselectedColor = SurfaceTheme.text.color
                            )
                        )
                    }

                    AnswerType.text -> {
                        var text by remember {
                            mutableStateOf("")
                        }
                        if (questionsAnswers[questionModel.questionNumber]!!.containsKey(answer.inputId)) {
                            text =
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId]!!
                        }
                        TextField(
                            value = text,
                            onValueChange = { change ->
                                text = change
                                questionsAnswers[questionModel.questionNumber]!![answer.inputId] =
                                    change
                            },
                            placeholder = {
                                ThemeText(text = "Введите ответ")
                            },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedTextColor = SurfaceTheme.text.color,
                                unfocusedTextColor = SurfaceTheme.text.color,
                                cursorColor = SurfaceTheme.text.color,
                                focusedPlaceholderColor = SurfaceTheme.disable.color,
                                unfocusedPlaceholderColor = SurfaceTheme.disable.color,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                containerColor = SurfaceTheme.button.color,
                                disabledTextColor = SurfaceTheme.disable.color
                            ),
                        )
                    }

                    AnswerType.select -> {

                    }
                }
                ThemeText(text = answer.text)
            }
            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}


@Composable
fun QuestionPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceTheme.foreground.color, RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        ThemeText(
            text = "Как создать это приложение?",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(
                    visible = true,
                    color = SurfaceTheme.placeholder_primary.color,
                    highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                    shape = RoundedCornerShape(15.dp)
                ),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(10.dp))
        Divider(color = SurfaceTheme.divider.color)
        Spacer(modifier = Modifier.size(20.dp))
        repeat(4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.placeholder(
                        visible = true,
                        color = SurfaceTheme.placeholder_primary.color,
                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                        shape = RoundedCornerShape(15.dp)
                    ),
                    colors = CheckboxDefaults.colors(
                        checkedColor = SurfaceTheme.text.color,
                        checkmarkColor = SurfaceTheme.button.color,
                        uncheckedColor = SurfaceTheme.text.color
                    )
                )
                ThemeText(
                    text = "Если бы я знал это, я бы не спрашивал", Modifier.placeholder(
                        visible = true,
                        color = SurfaceTheme.placeholder_primary.color,
                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                        shape = RoundedCornerShape(15.dp)
                    )
                )
            }
            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}