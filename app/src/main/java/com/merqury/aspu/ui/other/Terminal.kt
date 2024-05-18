package com.merqury.aspu.ui.other

import android.os.Bundle
import android.util.Log
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
import com.merqury.aspu.ui.magicState
import com.merqury.aspu.ui.navfragments.profile.showEiosAuthModalWindow
import com.merqury.aspu.ui.navfragments.settings.reloadSettingsScreen
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.navfragments.settings.toggleBooleanSettingsPreference
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.routeTo
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


var closeTerminal: () -> Unit = {}

class Terminal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerminalContent()
        }
        closeTerminal = {
            finish()
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
        if (line!!.contains(filteringString) || line!!.contains("D no-meta-info"))
            output.add(parseStdoutMessage(line!!))
    }
}

private fun parseStdoutMessage(line: String): TerminalStdoutMessage {
    val words = line.removeSpaces().split(" ")
    val date = LocalDate.parse(words[0] + ".2024", DateTimeFormatter.ofPattern("MM-dd.yyyy"))
    val time = LocalTime.parse(words[1], DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
    val tag = words[4]
    val type = words[5]
    var message = ""
    (6..<words.size).forEach {
        message += words[it] + " "
    }
    if (type == "no-meta-info:")
        return TerminalStdoutMessage("", message, type, tag)
    return TerminalStdoutMessage("", line, type, tag)
}

fun String.removeSpaces(): String{
    var res = trim()
    while (res.contains("  "))
        res = res.replace("  ", " ")
    return res
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
                    .background(SurfaceTheme.foreground.color)
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
                        textColor = SurfaceTheme.text.color,
                        cursorColor = SurfaceTheme.text.color,
                        placeholderColor = SurfaceTheme.disable.color,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            text = "Введите команду",
                            color = SurfaceTheme.disable.color
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
                .background(SurfaceTheme.background.color)
        ) {
            Divider(color = SurfaceTheme.divider.color)
            LazyColumn(
                Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                val list = output.toList().reversed()
                items(output.size) {
                    Text(text = list[it].message, color = SurfaceTheme.text.color)
                    Divider(color = SurfaceTheme.divider.color)
                }
            }
        }
    }
}

fun execCommand(command: String) {
    if (command.lowercase().trim() == "debug off") {
        toggleBooleanSettingsPreference("debug_mode")
        reloadSettingsScreen()
        printlog("Отключено, можете выходить отсюда")
        updateOutput()
        magicState.intValue = 3
        return
    }
    val args = command.lowercase().replace("\n", "").split(" ")
    when (args[0]) {
        "filter" -> filter(args[1])
        "echo" -> {
            if (args[1][0] == '$') {
                val varName = args[1].substring(1)
                if (varName == "all")
                    printAllPreferences()
                else printPreference(varName)
            } else printlog(args[1])
            updateOutput()
        }

        "fatal" -> throw RuntimeException(args[1])
        "set" -> {
            val name = args[1]
            val val_list = args.subList(2, args.size)
            val builder = StringBuilder()
            val_list.forEach {
                builder.append(it)
            }
            setPreference(name, builder.toString())
        }

        "reset" -> {
            val name = args[1]
            if (name == "all")
                settingsPreferences.edit().clear().apply()
            else
                settingsPreferences.edit().remove(name).apply()
        }

        "exit" -> {
            closeTerminal()
        }

        "eios" -> {
            closeTerminal()
            showEiosAuthModalWindow {
                routeTo("eios")
            }
        }

        "help" -> {
            helpMePlease()
        }
    }
}

fun helpMePlease() {
    filter("")
    Log.d("no-meta-info",
        """
        Привет
        Ты сейчас находишся в консоли приложения
        Все доступные тебе команды:
            - filter fatal : вывести причины недавних вылетов
            - echo <text> : вывести текст
            - echo $<name> : вывести значение переменной name
            - fatal : завершить работу приложения ошибкой (Зачем?)
            - set <name> <value> : установить в переменную name значение value
            - reset <name> : сбросить значение переменной до стандартного
            - exit : выйти из режма терминала
            - eios: вход в бета версию аккаунта ЭИОС, который сейчас находится в разработке
        В командах echo и reset можно использовать имя переменной all
        для того чтобы обратиться ко всем переменным
    """.trimIndent()
    )
    updateOutput()
}

fun setPreference(name: String, value: String) {
    settingsPreferences.edit().putString(name, value).apply()
}

fun printPreference(name: String) {
    printlog(settingsPreferences.getString(name, "$name undeclared!"))
    updateOutput()
}

fun printAllPreferences() {
    settingsPreferences.all.forEach {
        printlog("${it.key} = ${it.value}")
    }
    updateOutput()
}

fun filter(filter: String) {
    filteringString = when (filter) {
        "fatal" -> "FATAL"
        "err" -> "E"
        else -> "D debug-print"
    }
    updateOutput()
}