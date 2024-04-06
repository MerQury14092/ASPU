package com.merqury.aspu.ui.navfragments.messenger

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.merqury.aspu.appContext
import com.merqury.aspu.services.file.models.FileModel
import com.merqury.aspu.services.intents.FileOpener
import com.merqury.aspu.services.mailbox.outbox.getFioPrepod
import com.merqury.aspu.services.mailbox.outbox.getFioStudent
import com.merqury.aspu.services.mailbox.outbox.models.FioSearchElement
import com.merqury.aspu.services.mailbox.outbox.sendMessage
import com.merqury.aspu.ui.EditableText
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.other.ImageVectorButton
import com.merqury.aspu.ui.other.showFileUploadWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@Composable
fun SendMessageScreen(header: MutableState<@Composable () -> Unit>, onBack: () -> Unit) {
    var theme by remember { mutableStateOf("") }
    val text = rememberRichTextState()
    var userClassTo by remember {
        mutableIntStateOf(0)
    }
    val selectedFio = remember {
        mutableStateListOf<FioSearchElement>()
    }
    val attachedFiles = remember {
        mutableStateListOf<FileModel>()
    }
    header.value = {
        TitleHeader(title = "Новое сообщение")
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            ImageVectorButton(vector = Icons.Outlined.ArrowBack) {
                onBack()
            }

            Row {
                ImageVectorButton(vector = Icons.Outlined.Add) {
                    showFileUploadWindow {
                        attachedFiles.addAll(it)
                    }
                }
                ImageVectorButton(vector = Icons.Outlined.Send) {
                    if(selectedFio.isEmpty()) {
                        appContext!!.makeToast("Выберите получателя")
                        return@ImageVectorButton
                    }
                    if(theme.isEmpty())
                        theme = "Нет темы"
                    sendMessage(theme, text.toMarkdown(), selectedFio, attachedFiles)
                    onBack()
                }
            }
        }
    }
    Column (
        Modifier.verticalScroll(rememberScrollState())
    ){
        var expandedDropdown by remember {
            mutableStateOf(false)
        }
        val fioResults = remember {
            mutableStateListOf<FioSearchElement>()
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expandedDropdown = !expandedDropdown
            }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text =
                    when (userClassTo) {
                        0 -> "Кому  ⃰"
                        1 -> "Кому: сотруднику"
                        2 -> "Кому: студенту"
                        else -> "Кому"
                    }, modifier = Modifier.padding(
                        top = 3.dp, start = 5.dp
                    ),
                    color =
                    if (userClassTo == 0) SurfaceTheme.disable.color else SurfaceTheme.text.color
                )
                Image(
                    imageVector =
                    if (expandedDropdown)
                        Icons.Outlined.KeyboardArrowUp
                    else
                        Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 15.dp)
            ) {
                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(
                        extraSmall = RoundedCornerShape(15.dp)
                    )
                ) {
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = {
                            expandedDropdown = false
                        },
                        modifier = Modifier
                            .background(SurfaceTheme.foreground.color),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .background(SurfaceTheme.button.color, RoundedCornerShape(5.dp))
                                .fillMaxWidth()
                                .clickable {
                                    userClassTo = 1
                                    expandedDropdown = false
                                    fioResults.clear()
                                    selectedFio.clear()
                                }
                        ) {
                            Box(modifier = Modifier.padding(10.dp)) {
                                Text(text = "Сотруднику", color = SurfaceTheme.text.color)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .background(SurfaceTheme.button.color, RoundedCornerShape(5.dp))
                                .fillMaxWidth()
                                .clickable {
                                    userClassTo = 2
                                    expandedDropdown = false
                                    fioResults.clear()
                                    selectedFio.clear()
                                }
                        ) {
                            Box(modifier = Modifier.padding(10.dp)) {
                                Text(text = "Студенту", color = SurfaceTheme.text.color)
                            }
                        }
                    }
                }
            }
            Divider(color = SurfaceTheme.divider.color)
        }


        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            var fio by remember { mutableStateOf("") }
            selectedFio.forEach {
                FioElement(fio = it.fio!!, selected = true) {
                    selectedFio.remove(it)
                }
            }
            EditableText(enabled = !expandedDropdown && userClassTo != 0, value = fio, onChange = {
                fio = it
                if (userClassTo == 1) {
                    getFioPrepod(fio) { studentFio ->
                        fioResults.clear()
                        fioResults.addAll(studentFio)
                    }
                } else if (userClassTo == 2) {
                    getFioStudent(fio) { studentFio ->
                        fioResults.clear()
                        fioResults.addAll(studentFio)
                    }
                }
            }, placeholder = "ФИО  ⃰")
            if (fioResults.isNotEmpty()) {
                fioResults.forEach {
                    if (!selectedFio.contains(it))
                        FioElement(fio = it.fio!!, selected = false) {
                            selectedFio.add(it)
                        }
                }
            }
        }


        Divider(color = SurfaceTheme.divider.color)
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            EditableText(
                enabled = !expandedDropdown && userClassTo != 0,
                value = theme,
                onChange = { theme = it },
                placeholder = "Тема"
            )
        }
        Divider(color = SurfaceTheme.divider.color)
        /*Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = SurfaceTheme.foreground.color,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {

        }*/
        TextArea(
            state = text,
            modifier = Modifier.fillMaxSize(),
            placeholder = "Текст сообщения",
            enabled = !expandedDropdown && userClassTo != 0,
        )

        if(attachedFiles.isNotEmpty()){
            Spacer(modifier = Modifier.size(20.dp))
            Divider(color = SurfaceTheme.divider.color)
            Spacer(modifier = Modifier.size(20.dp))
            attachedFiles.forEach {
                Box(
                    modifier = Modifier
                        .background(
                            SurfaceTheme.foreground.color,
                            RoundedCornerShape(5.dp)
                        )
                        .padding(10.dp)
                        .clickable {
                            FileOpener.open(
                                appContext!!,
                                "http://plany.agpu.net${it.fileName}"
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.fileName, color = SurfaceTheme.text.color)
                }
                Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun FioElement(fio: String, selected: Boolean, onClick: () -> Unit = {}) {
    Box(
        Modifier
            .padding(5.dp)
            .clickable {
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .background(SurfaceTheme.foreground.color, RoundedCornerShape(15.dp))
                .fillMaxWidth()
                .height(50.dp)
                .padding(10.dp)
        ) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemeText(text = fio)
                if(selected)
                    ImageVectorButton(vector = Icons.Outlined.Close) {onClick()}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextArea(
    state: RichTextState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true
) {
    RichTextEditor(
        enabled = enabled,
        state = state,
        modifier = Modifier
            .padding(10.dp)
            .then(modifier)
            .border(
                width = 1.dp,
                color = SurfaceTheme.foreground.color,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = RichTextEditorDefaults.richTextEditorColors(
            textColor = SurfaceTheme.text.color,
            cursorColor = SurfaceTheme.text.color,
            placeholderColor = SurfaceTheme.disable.color,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            containerColor = SurfaceTheme.background.color,
            disabledTextColor = SurfaceTheme.disable.color
        ),
        placeholder = {
            Text(text = placeholder, color = SurfaceTheme.disable.color)
        }
    )
}