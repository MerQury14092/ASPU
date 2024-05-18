package com.merqury.aspu.ui.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.file.allocate
import com.merqury.aspu.services.file.downloadFile
import com.merqury.aspu.services.file.longPollingGetFiles
import com.merqury.aspu.services.file.models.FileModel
import com.merqury.aspu.ui.showSimpleUpdatableModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.vw
import java.util.stream.Collectors

fun showFileUploadWindow(onSelectFile: (List<FileModel>) -> Unit) {
    showSimpleUpdatableModalWindow { showed, update, forUpdate ->
        var allocatedId by rememberSaveable {
            mutableIntStateOf(-1)
        }
        val files = remember {
            mutableStateListOf<FileModel>()
        }
        Box(
            modifier = Modifier
                .size(90.vw)
                .background(SurfaceTheme.background.color)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (files.isEmpty()) Arrangement.SpaceEvenly else Arrangement.Top
            ) {
                if (allocatedId == -1) {
                    Box(
                        modifier = Modifier
                            .background(
                                SurfaceTheme.foreground.color,
                                RoundedCornerShape(15.dp)
                            )
                            .padding(15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Загрузить с устройства",
                            color = SurfaceTheme.text.color,
                            fontSize = 16.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                SurfaceTheme.foreground.color,
                                RoundedCornerShape(15.dp)
                            )
                            .padding(15.dp)
                            .clickable {
                                allocate {
                                    allocatedId = it
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Загрузить с компьютера",
                            color = SurfaceTheme.text.color,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    forUpdate.value
                    if (files.isEmpty()) {
                        longPollingGetFiles(allocatedId) {
                            files.clear()
                            files.addAll(it)
                            update()
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    SurfaceTheme.foreground.color,
                                    RoundedCornerShape(15.dp)
                                )
                                .padding(15.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Введите эту ссылку в браузере вашего ПК",
                                color = SurfaceTheme.text.color,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    SurfaceTheme.foreground.color,
                                    RoundedCornerShape(15.dp)
                                )
                                .width(50.vw)
                                .height(40.vw),
                            contentAlignment = Alignment.Center
                        ) {
                            var link = "eios.site"
                            if (allocatedId != 0)
                                link += "/$allocatedId"
                            Text(
                                text = link,
                                color = SurfaceTheme.link.color,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Внимание! Ссылка работает только 5 минут, если не успели, перезапустите данное окно",
                            color = SurfaceTheme.text.color,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            ) {
                                files.forEach {
                                    Row(
                                        modifier = Modifier.padding(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth(.9f)) {
                                            FileView(file = it)
                                        }
                                        forUpdate.value
                                        if (it.content == null) {
                                            downloadFile(allocatedId, it) {
                                                update()
                                            }
                                            CircularProgressIndicator(color = SurfaceTheme.text.color)
                                        } else {
                                            Image(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(Color.Green)
                                            )
                                        }
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clickable {
                                                onSelectFile(
                                                    files
                                                        .stream()
                                                        .filter { it.content != null }
                                                        .collect(Collectors.toList())
                                                )
                                            }
                                            .width(50.vw)
                                            .height(10.vw)
                                            .background(
                                                SurfaceTheme.button.color,
                                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                            )
                                            .border(
                                                2.dp,
                                                color = SurfaceTheme.divider.color,
                                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ThemeText(text = "Прикрепить")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}