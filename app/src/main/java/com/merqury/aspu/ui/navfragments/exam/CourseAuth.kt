package com.merqury.aspu.ui.navfragments.exam

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.services.exam.cancelAllLoginTries
import com.merqury.aspu.services.exam.loginToCourse
import com.merqury.aspu.ui.after
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
fun showCourseAuthModalWindow(
    msg: String = "",
    course: ExamCourse,
    closure: () -> Unit = {}
) {
    showSimpleModalWindow(
        closeable = true
    ) {
        val password = remember {
            mutableStateOf("")
        }
        var requesting by remember {
            mutableStateOf(false)
        }
        var receivedResponse by remember {
            mutableStateOf(false)
        }
        var authSuccess by remember {
            mutableStateOf(false)
        }
        var authMessage by remember {
            mutableStateOf(msg)
        }
        if (!authSuccess) {
            cancelAllLoginTries()
            receivedResponse = false
            requesting = true
            loginToCourse(
                course.id,
                password.value,
                {},
                {
                    printlog("Подобран правильный пароль")
                    cancelAllLoginTries()
                    requesting = false
                    receivedResponse = true
                    authSuccess = true
                    password.value = it

                }
            ) {
                requesting = false
                receivedResponse = true
                authSuccess = it
            }
        }
        Box(modifier = Modifier.background(SurfaceTheme.background.color)) {
            Column(
                Modifier
                    .fillMaxWidth(.7f)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    Icons.Rounded.AccountCircle,
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp),
                    colorFilter = ColorFilter.tint(
                        SurfaceTheme.text.color
                    )
                )
                Text(
                    text = course.name,
                    fontSize = 20.sp,
                    color = SurfaceTheme.text.color,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(20.dp))
                TextField(
                    value = password.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = {
                        password.value = it
                        authMessage = ""
                    },
                    enabled = !authSuccess,
                    placeholder = { Text("Кодовое слово") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = SurfaceTheme.text.color,
                        unfocusedTextColor = SurfaceTheme.text.color,
                        cursorColor = SurfaceTheme.text.color,
                        focusedPlaceholderColor = SurfaceTheme.disable.color,
                        unfocusedPlaceholderColor = SurfaceTheme.disable.color,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        containerColor = SurfaceTheme.foreground.color,
                        disabledTextColor = SurfaceTheme.disable.color
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(5.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Button(
                        onClick = {
                            if (!requesting)
                                it.value = false
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceTheme.button.color
                        )
                    ) {
                        Text(
                            text = "Отмена",
                            color = if (requesting) SurfaceTheme.disable.color else SurfaceTheme.text.color
                        )
                    }

                    Button(
                        onClick = {}, colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceTheme.button.color
                        )
                    ) {
                        if (!receivedResponse) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SurfaceTheme.text.color
                            )
                        } else if (authSuccess) {
                            Image(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    SurfaceTheme.text.color
                                )
                            )
                            after(2.seconds) {
                                it.value = false
                                closure()
                            }
                        } else
                            Image(
                                Icons.Rounded.Close,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    SurfaceTheme.text.color
                                )
                            )
                    }
                }
                if (authMessage.isNotEmpty())
                    Text(text = authMessage, color = SurfaceTheme.text.color)
            }
        }
    }
}