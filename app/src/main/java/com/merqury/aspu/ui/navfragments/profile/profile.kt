package com.merqury.aspu.ui.navfragments.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.services.profile.getProfileInfo
import com.merqury.aspu.services.profile.models.ProfileInfo
import com.merqury.aspu.ui.TitleHeader
import com.merqury.aspu.ui.navfragments.settings.SettingsActivity
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.toggle

val secretPreferences: SharedPreferences =
    appContext!!.getSharedPreferences("secret", Context.MODE_PRIVATE)

var profileInfo: ProfileInfo? by mutableStateOf(null)

@Composable
fun ProfileScreen(header: MutableState<@Composable () -> Unit>) {
    header.value = {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            TitleHeader(title = "Профиль")
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.settings_icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        appContext!!.startActivity(Intent(appContext!!, SettingsActivity::class.java))
                    },
                colorFilter = ColorFilter.tint(
                    com.merqury.aspu.ui.theme.SurfaceTheme.enable.color
                )
            )
            Image(
                Icons.Rounded.MailOutline,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(30.dp),
                colorFilter = ColorFilter.tint(
                    com.merqury.aspu.ui.theme.SurfaceTheme.enable.color
                )
            )
        }
    }
    val forUpdate = remember {
        mutableStateOf(false)
    }
    forUpdate.value
    if ((profileInfo?.state ?: -1) != 1L) {
        ProfileInfoPlaceholder()
        if(secretPreferences.contains("authToken"))
            getProfileInfo(
                secretPreferences.getString("authToken", null)!!,
                secretPreferences.getInt("userId", 0)
            ){
                profileInfo = it
            }
        else
            showEiosAuthModalWindow {
                forUpdate.toggle()
            }
    }
    else {
        com.merqury.aspu.ui.navfragments.profile.ProfileInfo(info = profileInfo?.data!!)
    }
}



