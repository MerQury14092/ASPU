package com.merqury.aspu.ui.navfragments.exam.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.services.exam.models.ExamUserModel
import com.merqury.aspu.services.exam.moodleCookie
import com.merqury.aspu.ui.navfragments.exam.CourseItem
import com.merqury.aspu.ui.navfragments.exam.InitialAvatar
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
internal fun ProfileContent(
    info: ExamUserModel
) {
    val imageSize = 100.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(SurfaceTheme.background.color)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(imageSize / 2))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            SurfaceTheme.foreground.color,
                            shape = RoundedCornerShape(30.dp, 30.dp)
                        )
                        .fillMaxHeight()
                        .padding(10.dp)
                ) {
                    Spacer(modifier = Modifier.size(imageSize / 2))
                    ThemeText(
                        text = "${info.lastName} ${info.firstName}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.size(40.dp))
                    InfoBlock(title = "Электронная почта", info = info.email)
                    InfoBlock(title = "Страна", info = info.country)
                    InfoBlock(title = "Город", info = info.city)
                    CoursesList(list = info.courses)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageSize),
                contentAlignment = Alignment.Center
            ) {
                Avatar(imageSize = imageSize, info = info)
                info.lastLogin?.let {
                    LastLogin(it)
                }
            }
        }
    }
}

@Composable
private fun Avatar(
    imageSize: Dp,
    info: ExamUserModel
) {
    Box(
        modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
            .background(SurfaceTheme.foreground.color),
        contentAlignment = Alignment.Center
    ) {
        if (info.photoUrl != null)
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(info.photoUrl)
                    .addHeader("cookie", moodleCookie())
                    .build(),
                contentDescription = "",
                loading = {
                    InitialAvatar(
                        lastName = info.lastName,
                        firstName = info.firstName,
                        size = imageSize - 10.dp
                    )
                },
                modifier = Modifier
                    .size(imageSize - 10.dp)
                    .clip(CircleShape),
            )
        else
            InitialAvatar(
                lastName = info.lastName,
                firstName = info.firstName,
                size = imageSize - 10.dp
            )
    }
}

@Composable
private fun LastLogin(lastLogin: LocalDateTime) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .fillMaxSize(.5f)
                .padding(start = 20.dp, bottom = 5.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                modifier = Modifier
                    .background(SurfaceTheme.foreground.color, shape = CircleShape)
                    .padding(2.dp)
                    .clip(
                        CircleShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .background(SurfaceTheme.button.color)
                        .padding(3.dp)
                        .clip(
                            CircleShape
                        )
                ) {
                    ThemeText(text = whenLastLogin(lastLogin), fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun InfoBlock(title: String, info: String?) {
    if (info != null) {
        ThemeText(
            text = "$title:",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.size(5.dp))
        ThemeText(
            text = info,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun CoursesList(
    list: List<ExamCourse>
) {
    Column {
        ThemeText(
            text = "Участник экзаменов:",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        list.forEach {
            CourseItem(item = it, isMyCourse = false)
        }
        if (list.isEmpty()) {
            Spacer(modifier = Modifier.size(5.dp))
            ThemeText(
                text = "Не учавствует ни в одном",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontSize = 15.sp
            )
        }

    }
}

private fun whenLastLogin(lastLogin: LocalDateTime): String {
    val zoneId = ZoneId.of("Europe/Moscow")
    val now = LocalDateTime.now().atZone(zoneId).toInstant().toEpochMilli()
    val login = lastLogin.atZone(zoneId).toInstant().toEpochMilli()
    return when(val diff = (now-login)/1000) {
        in 0..<60               -> "$diff сек."
        in 60..<3600            -> "${diff/60} мин."
        in 3600..<86400         -> "${diff/3600} час."
        in 86400..<604800       -> "${diff/86400} сут."
        in 604800..<2419200     -> "${diff/604800} нед."
        in 2419200..<29030400   -> "${diff/2419200} мес."
        in 29030400..<2903040000-> "${diff/29030400} лет"
        else -> "N/A"
    }
}