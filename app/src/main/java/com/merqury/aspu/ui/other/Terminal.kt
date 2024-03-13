package com.merqury.aspu.ui.other

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class Terminal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerminalContent()
        }
    }
}

@Composable
@Preview
private fun TerminalContentPreview() {
    TerminalContent()
}

var filteringString = "D debug-print"

private data class TerminalStdoutMessage(
    val dateTime: String,
    val message: String,
    val type: String,
    val tag: String
)

private val output = mutableStateListOf<TerminalStdoutMessage>()
private fun updateOutput() {
    output.clear()
    val process = Runtime.getRuntime().exec("logcat -d")
    val bufferedReader = BufferedReader(
        InputStreamReader(process.inputStream)
    )
    var line: String?
    while (bufferedReader.readLine().also { line = it } != null) {
        if (line!!.contains(filteringString))
            output.add(parseStdoutMessage(line!!))
    }
}

private fun parseStdoutMessage(line: String): TerminalStdoutMessage {
    val words = line.split(" ")
    val date = LocalDate.parse(words[0]+".2024", DateTimeFormatter.ofPattern("MM-dd.yyyy"))
    val time = LocalTime.parse(words[1], DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
    val tag = words[4]
    val type = words[5]
    var message = ""
    (6..<words.size).forEach {
        message += words[it]+" "
    }
    return TerminalStdoutMessage("", line, type, tag)
}

@Composable
private fun TerminalContent() {
    val inputField = remember {
        mutableStateOf("")
    }
    updateOutput()
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(theme.value[SurfaceTheme.foreground]!!)
                    .fillMaxHeight(.06f)
            ) {
                val gradientColors = listOf(
                    Color.Red,
                    Color.Magenta,
                    Color.Blue,
                    Color.Cyan,
                    Color.Green,
                    Color.Yellow,
                    Color.Red
                )
                TextField(
                    value = inputField.value,
                    onValueChange = {
                        if (it.contains("\n")) {
                            inputField.value = ""
                            execCommand(it)
                        } else
                            inputField.value = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = theme.value[SurfaceTheme.text]!!,
                        cursorColor = theme.value[SurfaceTheme.text]!!,
                        placeholderColor = theme.value[SurfaceTheme.disable]!!,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            text = "Введите команду",
                            color = theme.value[SurfaceTheme.disable]!!
                        )
                    },
                    textStyle = TextStyle(remember {
                        Brush.linearGradient(
                            colors = gradientColors
                        )
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddings ->
        Box(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
                .background(theme.value[SurfaceTheme.background]!!)
        ) {
            Divider(color = theme.value[SurfaceTheme.divider]!!)
            LazyColumn(
                Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                val list = output.toList().reversed()
                items(output.size) {
                    Text(text = list[it].message, color = theme.value[SurfaceTheme.text]!!)
                    Divider(color = theme.value[SurfaceTheme.divider]!!)
                }
            }
        }
    }
}

fun execCommand(command: String) {
    val args = command.replace("\n", "").split(" ")
    when (args[0]) {
        "filter" -> filter(args[1])
        "echo" -> {
            printlog(args[1])
            updateOutput()
        }

        "fatal" -> throw RuntimeException(args[1])
    }
}

fun filter(filter: String) {
    filteringString = when (filter) {
        "fatal" -> "FATAL"
        "err" -> "E"
        else -> "D debug-print"
    }
    updateOutput()
}