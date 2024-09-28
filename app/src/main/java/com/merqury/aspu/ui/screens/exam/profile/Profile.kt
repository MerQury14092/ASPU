package com.merqury.aspu.ui.screens.exam.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.merqury.aspu.appContext
import com.merqury.aspu.services.exam.getUserInfo
import com.merqury.aspu.services.exam.models.ExamUserModel
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.printlog
import com.merqury.aspu.ui.startTopBarActivityWithActivityLink

fun startProfileScreen() {
    startProfileScreen(null)
}

fun startProfileScreen(userId: Int?) {
    appContext.startTopBarActivityWithActivityLink() { header, link ->
        Profile(userId = userId, header) {
            link!!.finish()
        }
    }
}

@Composable
private fun Profile(
    userId: Int?,
    header: MutableState<@Composable () -> Unit>,
    finish: () -> Unit
) {
    var loaded by remember {
        mutableStateOf(false)
    }
    var success by remember {
        mutableStateOf(false)
    }
    var info by remember {
        mutableStateOf<ExamUserModel?>(null)
    }
    if (!loaded) {
        if (userId == null)
            getUserInfo({
                loaded = true
                success = false
                printlog(it)
            }) {
                loaded = true
                success = true
                info = it
            }
        else
            getUserInfo(userId, {
                loaded = true
                success = false
            }) {
                loaded = true
                success = true
                info = it
            }

        ProfileContentPlaceholder()
    } else {
        if (!success) {
            finish()
            appContext!!.makeToast("Error!")
        } else {
            ProfileContent(info = info!!)
        }
    }
}