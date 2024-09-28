package com.merqury.aspu.ui.screens.exam

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.exam.models.ExamMemberModel
import com.merqury.aspu.services.exam.moodleCookie
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.screens.exam.profile.startProfileScreen
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.theme.colorWithoutAnim

fun showExamMembers(
    members: List<ExamMemberModel>
) {
    showSimpleModalWindow(
        containerColor = SurfaceTheme.background.colorWithoutAnim
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.92f)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                val sorted = members.sortedBy { it.lastName.lowercase() }
                sorted.forEach { model ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceTheme.foreground.color
                        ),
                        modifier = Modifier.padding(5.dp).bounceClick {
                            startProfileScreen(model.id.toInt())
                            it.value = false
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (model.photoUrl == null)
                                    InitialAvatar(
                                        lastName = model.lastName,
                                        firstName = model.firstName,
                                        40.dp
                                    )
                                else
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(model.photoUrl)
                                            .addHeader("cookie", moodleCookie())
                                            .build(),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(40.dp).clip(CircleShape),
                                        loading = {
                                            InitialAvatar(
                                                lastName = model.lastName,
                                                firstName = model.firstName,
                                                40.dp
                                            )
                                        }
                                    )
                                Spacer(modifier = Modifier.size(10.dp))
                                ThemeText(text = "${model.lastName} ${model.firstName}")
                            }
                            if (model.id == AppConfig.getDeveloperExamProfileId())
                                Image(
                                    Icons.Rounded.Star,
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(Color(0xff5C9CE6))
                                )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InitialAvatar(
    lastName: String,
    firstName: String,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(SurfaceTheme.disable.color),
        contentAlignment = Alignment.Center
    ) {
        ThemeText(text = lastName[0].uppercase() + firstName[0].uppercase())
    }
}