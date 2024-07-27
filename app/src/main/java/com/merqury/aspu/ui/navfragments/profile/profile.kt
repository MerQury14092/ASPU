package com.merqury.aspu.ui.navfragments.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import com.merqury.aspu.ui.bounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.canopas.lib.showcase.IntroShowcase
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.services.profile.getProfileInfo
import com.merqury.aspu.services.profile.models.ProfileInfo
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.messenger.MessengerScreen
import com.merqury.aspu.ui.navfragments.messenger.messagesLoaded
import com.merqury.aspu.ui.navfragments.settings.SettingsActivity
import com.merqury.aspu.ui.startActivity
import com.merqury.aspu.ui.startTopBarActivity
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.toggle
import com.merqury.aspu.ui.training.TrainingStates
import com.merqury.aspu.ui.training.hintTargetModifier

val secretPreferences: SharedPreferences =
    appContext!!.getSharedPreferences("secret", Context.MODE_PRIVATE)

var profileInfo: ProfileInfo? by mutableStateOf(null)

@Composable
fun ProfileScreen(header: MutableState<@Composable () -> Unit>) {
    val headerContent = @Composable {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TitleHeader(title = "Профиль")
        }
        IntroShowcase(
            showIntroShowCase = TrainingStates.accountHeader,
            onShowCaseCompleted = {
                TrainingStates.aspuButtonDescription =
                    "Открывает мобильную версию ЭИОС. Если используете встроенный браузер, то " +
                            "откроется сразу авторизованная страница"
                TrainingStates.aspuButtonHintClosure = {
                    TrainingStates.isTraining = false
                }
                TrainingStates.aspuButton = true
                TrainingStates.accountHeader = false
            }) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(30.dp)
                        .bounceClick {
                            appContext!!.startActivity(SettingsActivity::class.java)
                        }
                        .then(
                            hintTargetModifier(
                                0,
                                "Настройки",
                                "Настройки приложения"
                            )
                        ),
                    colorFilter = ColorFilter.tint(
                        com.merqury.aspu.ui.theme.SurfaceTheme.enable.color
                    )
                )
                Image(
                    Icons.Rounded.MailOutline,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(30.dp)
                        .bounceClick {
                            messagesLoaded = false
                            appContext.startTopBarActivity {
                                MessengerScreen(header = it)
                            }
                        },
                    colorFilter = ColorFilter.tint(
                        com.merqury.aspu.ui.theme.SurfaceTheme.enable.color
                    )
                )
            }
        }
    }
    if (header.value != headerContent)
        header.value = headerContent
    val forUpdate = remember {
        mutableStateOf(false)
    }
    var loading by remember {
        mutableStateOf(false)
    }
    forUpdate.value
    if ((profileInfo?.state ?: -1) != 1L) {
        ProfileInfoPlaceholder()
        if (secretPreferences.contains("authToken")) {
            if (!loading) {
                loading = true
                getProfileInfo(
                    secretPreferences.getString("authToken", null)!!,
                    secretPreferences.getInt("userId", 0),
                    onClosure = {
                        forUpdate.toggle()
                        loading = false
                    }
                ) {
                    profileInfo = it
                }
            }
        } else {
                showEiosAuthModalWindow {
                    forUpdate.toggle()
                }
            }
    } else {
        ProfileInfo(info = profileInfo?.data!!)
    }
}



