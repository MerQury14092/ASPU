package com.merqury.aspu.ui.navfragments.profile


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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.services.profile.getAuthToken
import com.merqury.aspu.ui.after
import com.merqury.aspu.ui.navBarUpdate
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
fun showEiosAuthModalWindow(msg: String = "", closure: () -> Unit = {}) {
    showSimpleModalWindow(
        closeable = false
    ) {
        val username = remember {
            mutableStateOf("")
        }

        fun String.hide(): String {
            return "•".repeat(length);
        }

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
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        if (receivedResponse) {
            after(2.seconds) {
                if (authSuccess) {
                    it.value = false
                    return@after
                }
                requesting = false
                receivedResponse = false
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
                    text = "Вход в аккаунт ЭИОС",
                    fontSize = 20.sp,
                    color = SurfaceTheme.text.color
                )
                Spacer(modifier = Modifier.size(20.dp))
                TextField(
                    value = username.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = {
                        username.value = it
                        authMessage = ""
                    },
                    enabled = !requesting,
                    placeholder = { Text("Логин") },
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
                TextField(
                    value = password.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = {
                        password.value = it
                        authMessage = ""
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    enabled = !requesting,
                    placeholder = { Text("Пароль") },
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
                    trailingIcon = {
                        val image = if (passwordVisible)
                            R.drawable.passvisible
                        else R.drawable.passvisibleoff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Image(
                                painterResource(id = image), description,
                                colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
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
                        onClick = {
                            if (requesting)
                                return@Button
                            if (username.value.isEmpty() || password.value.isEmpty()) {
                                authMessage = "Поля не должны быть пустыми"
                                return@Button
                            }
                            requesting = true
                            getAuthToken(
                                username.value,
                                password.value,
                                { token, id ->
                                    receivedResponse = true
                                    authSuccess = true
                                    secretPreferences
                                        .edit()
                                        .putString("authToken", token)
                                        .putInt("userId", id)
                                        .putString("login", username.value)
                                        .putString("password", password.value)
                                        .apply()
                                    settingsPreferences.edit()
                                        .putBoolean("eios_logged", true)
                                        .apply()
                                    navBarUpdate()
                                    closure()
                                },
                                {
                                    receivedResponse = true
                                    authSuccess = false
                                    authMessage = it
                                }
                            )
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceTheme.button.color
                        )
                    ) {
                        if (!requesting) {
                            Text(text = "Войти", color = SurfaceTheme.text.color)
                        } else if (!receivedResponse) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SurfaceTheme.text.color
                            )
                        } else if (authSuccess)
                            Image(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    SurfaceTheme.text.color
                                )
                            )
                        else
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