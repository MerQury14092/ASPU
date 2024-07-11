package com.merqury.aspu.ui.navfragments.exam

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.merqury.aspu.R
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.exam.getProfilePhotoUrl
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.vw
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamHeader() {
    val coroutineScope = rememberCoroutineScope()
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Button(
            onClick = {
                requestQueue!!.cancelAll("load courses")
                coroutineScope.launch {
                    pager.animateScrollToPage(
                        if (pager.currentPage == 0) 1 else 0,
                        animationSpec = tween(1200)
                    )
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceTheme.button.color
            )
        ) {
            ThemeText(
                text = if (pager.currentPage == 1) "Мои экзамены" else "Все экзамены",
                fontSize = 11.sp
            )
        }
        Box(
            modifier = Modifier
                .background(
                    SurfaceTheme.button.color,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp, 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val firstName = settingsPreferences.getString("exam-first-name", "Имя")!!
                    val lastName = settingsPreferences.getString("exam-last-name", "Фамилия")!!
                    ThemeText(
                        text = "$lastName ${firstName[0]}.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
                var photoUrl by remember {
                    mutableStateOf<String?>(
                        null
                    )
                }
                if (photoUrl == null) {
                    getProfilePhotoUrl {
                        photoUrl = it
                    }
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(12.vw)
                    )
                } else
                    SubcomposeAsyncImage(
                        model = photoUrl!!, contentDescription = "", loading = {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(12.vw)
                            )
                        }, modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxHeight()
                    )
            }
        }

    }
}