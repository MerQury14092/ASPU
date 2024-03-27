package com.merqury.aspu.ui.navfragments.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.R
import com.merqury.aspu.services.profile.getAvg
import com.merqury.aspu.services.profile.getMarkStatsById
import com.merqury.aspu.services.profile.models.Data
import com.merqury.aspu.services.profile.models.MarkStat
import com.merqury.aspu.ui.placeholder
import com.merqury.aspu.ui.sp
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.vw


private var marksStat: MarkStat? by mutableStateOf(null)

@Composable
fun ProfileInfo(info: Data) {
    Box(
        Modifier
            .background(SurfaceTheme.background.color)
            .fillMaxSize()
    ) {
        info.apply {
            if (marksStat == null) {
                getMarkStatsById(studentID!!, onError = {
                }) {
                    marksStat = it
                }
            }
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                val space = 20.dp
                Box(
                    modifier = Modifier.background(
                        SurfaceTheme.foreground.color,
                        shape = RoundedCornerShape(
                            topStart = CornerSize(0),
                            topEnd = CornerSize(0),
                            bottomStart = CornerSize(15.dp),
                            bottomEnd = CornerSize(15.dp)
                        )
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_background),
                        contentDescription = null,
                        modifier = Modifier.clip(
                            shape = RoundedCornerShape(
                                topStart = CornerSize(0),
                                topEnd = CornerSize(0),
                                bottomStart = CornerSize(15.dp),
                                bottomEnd = CornerSize(15.dp)
                            )
                        )
                    )
                    Column {
                        Spacer(modifier = Modifier.size(20.dp))
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = "http://plany.agpu.net${photoLink ?: ""}",
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.man),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(100.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.size(space))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row {
                                    Text(
                                        text = surname ?: "Фамилия",
                                        color = SurfaceTheme.text.color,
                                        fontSize = 24.sp
                                    )
                                    if (middleName == null) {
                                        Text(
                                            text = " ${name ?: "Имя"}",
                                            color = SurfaceTheme.text.color,
                                            fontSize = 24.sp
                                        )
                                    }
                                }
                                middleName?.also {
                                    Row {
                                        Text(
                                            text = "${name ?: "Имя"} $middleName",
                                            color = SurfaceTheme.text.color,
                                            fontSize = 24.sp
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(30.dp))
                    }
                }
                Spacer(modifier = Modifier.size(space / 2))
//                Column(
//                    Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.End
//                ) {
//                    Text(
//                        text = "Последний вход: ${lastEnterDate ?: "неизвестно"}",
//                        color = SurfaceTheme.text.color
//                    )
//                }
                Spacer(modifier = Modifier.size(space))
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceTheme.foreground.color, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.student_cap),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(50.dp),
                            colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var avg = "5"

                            marksStat?.data?.markCountStatistic?.also {
                                avg = getAvg(it)
                            }

                            Text(text = "Средний балл:", color = SurfaceTheme.text.color)
                            Text(
                                text = avg,
                                color = SurfaceTheme.text.color,
                                fontSize = 24.sp,
                                modifier = Modifier.placeholder(marksStat == null)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    @Composable
                    fun MarkBox(
                        name: String,
                        value: String,
                        count: Int
                    ) {
                        var collapsing by remember {
                            mutableStateOf(false)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    SurfaceTheme.foreground.color,
                                    RoundedCornerShape(15.dp)
                                )
                                .padding(10.dp)
                                .width(70.dp)
                                .clickable {
                                    collapsing = !collapsing
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = name, color = SurfaceTheme.text.color)
                                Text(
                                    text = value,
                                    color = SurfaceTheme.text.color,
                                    modifier = Modifier.placeholder(visible = marksStat == null)
                                )
                                if (collapsing)
                                    Text(text = "$count оц.", color = SurfaceTheme.text.color)
                            }
                        }
                    }

                    var markAAvg = "100%"
                    var markBAvg = "100%"
                    var markCAvg = "100%"
                    var markACount = 0
                    var markBCount = 0
                    var markCCount = 0
                    marksStat?.also {
                        markAAvg = "-"
                        markBAvg = "-"
                        markCAvg = "-"
                    }
                    marksStat?.data?.markCountStatistic?.onEach {
                        when (it.markName) {
                            "Удовл" -> {
                                markCAvg = "${it.avg}%"
                                markCCount = it.count.toInt()
                            }

                            "Хор" -> {
                                markBAvg = "${it.avg}%"
                                markBCount = it.count.toInt()
                            }

                            "Отл" -> {
                                markAAvg = "${it.avg}%"
                                markACount = it.count.toInt()
                            }
                        }
                    }
                    MarkBox(name = "Удовл", value = markCAvg, markCCount)
                    MarkBox(name = "Хор", value = markBAvg, markBCount)
                    MarkBox(name = "Отл", value = markAAvg, markACount)
                }
                Spacer(modifier = Modifier.size(30.dp))
                Column {
                    @Composable
                    fun ProfileItem(text: String) {
                        Column(
                            Modifier.padding(vertical = space / 4)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        SurfaceTheme.foreground.color,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(text = text, color = SurfaceTheme.text.color)
                            }
                        }
                    }

                    val profileCardButtonSize = 27

                    @Composable
                    fun ProfileCardButton(
                        text: String,
                        icon: Int
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    SurfaceTheme.foreground.color,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .size(profileCardButtonSize.vw),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(12.vw)
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    text = text,
                                    color = SurfaceTheme.text.color,
                                    modifier = Modifier.width(17.vw),
                                    textAlign = TextAlign.Center,
                                    fontSize = 3.vw.sp
                                )
                            }
                        }
                    }

                    @Composable
                    fun RowSpaceEvenly(content: @Composable () -> Unit) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            content()
                        }
                    }

                    Divider(color = SurfaceTheme.divider.color)
                    Spacer(modifier = Modifier.size(20.dp))
                    RowSpaceEvenly {
                        ProfileCardButton("О себе", R.drawable.user)
                        ProfileCardButton("Оценки", R.drawable.book_alt)
                        ProfileCardButton("Уч. план", R.drawable.study_plan)
                    }
                    Spacer(modifier = Modifier.size(((100-profileCardButtonSize.toDouble()*3)/4).vw))
                    RowSpaceEvenly {
                        ProfileCardButton("Портфолио", R.drawable.trophy)
                        ProfileCardButton("Методички", R.drawable.book)
                        ProfileCardButton("Выйти", R.drawable.back)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoPlaceholder() {
    Box(
        Modifier
            .background(SurfaceTheme.background.color)
            .fillMaxSize()
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            val space = 20.dp
            Box(
                modifier = Modifier.background(
                    SurfaceTheme.foreground.color,
                    shape = RoundedCornerShape(
                        topStart = CornerSize(0),
                        topEnd = CornerSize(0),
                        bottomStart = CornerSize(15.dp),
                        bottomEnd = CornerSize(15.dp)
                    )
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_background),
                    contentDescription = null,
                    modifier = Modifier.clip(
                        shape = RoundedCornerShape(
                            topStart = CornerSize(0),
                            topEnd = CornerSize(0),
                            bottomStart = CornerSize(15.dp),
                            bottomEnd = CornerSize(15.dp)
                        )
                    )
                )
                Column {
                    Spacer(modifier = Modifier.size(20.dp))
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            contentDescription = null,
                            painter = painterResource(id = R.drawable.man),
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                                .placeholder(
                                    visible = true,
                                    color = SurfaceTheme.placeholder_primary.color,
                                    highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.size(space))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row {
                                Text(
                                    text = "Фамилия",
                                    color = SurfaceTheme.text.color,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .placeholder(
                                            visible = true,
                                            color = SurfaceTheme.placeholder_primary.color,
                                            highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                )
                            }
                            Row {
                                Text(
                                    text = "Антошка",
                                    color = SurfaceTheme.text.color,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .placeholder(
                                            visible = true,
                                            color = SurfaceTheme.placeholder_primary.color,
                                            highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                )
                                Text(
                                    text = "Павленыч",
                                    color = SurfaceTheme.text.color,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .placeholder(
                                            visible = true,
                                            color = SurfaceTheme.placeholder_primary.color,
                                            highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.size(space / 2))
//                Column(
//                    Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.End
//                ) {
//                    Text(
//                        text = "Последний вход: ${lastEnterDate ?: "неизвестно"}",
//                        color = SurfaceTheme.text.color
//                    )
//                }
            Spacer(modifier = Modifier.size(space))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceTheme.foreground.color, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.student_cap),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(50.dp),
                        colorFilter = ColorFilter.tint(SurfaceTheme.text.color)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Средний балл:", color = SurfaceTheme.text.color)
                        Text(
                            text = "5",
                            color = SurfaceTheme.text.color,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .placeholder(
                                    visible = true,
                                    color = SurfaceTheme.placeholder_primary.color,
                                    highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                    shape = RoundedCornerShape(15.dp)
                                )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                @Composable
                fun MarkBox(
                    name: String,
                    value: String
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                SurfaceTheme.foreground.color,
                                RoundedCornerShape(15.dp)
                            )
                            .padding(10.dp)
                            .width(70.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = name, color = SurfaceTheme.text.color)
                            Text(
                                text = value, color = SurfaceTheme.text.color, modifier = Modifier
                                    .placeholder(
                                        visible = true,
                                        color = SurfaceTheme.placeholder_primary.color,
                                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                        shape = RoundedCornerShape(15.dp)
                                    )
                            )
                        }
                    }
                }
                MarkBox(name = "Удовл.", value = "100%")
                MarkBox(name = "Хор.", value = "100%")
                MarkBox(name = "Отл.", value = "100%")
            }
            Spacer(modifier = Modifier.size(30.dp))
            Column {
                @Composable
                fun ProfileItem(text: String) {
                    Column(
                        Modifier.padding(vertical = space / 4)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    SurfaceTheme.foreground.color,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = text,
                                color = SurfaceTheme.text.color,
                                modifier = Modifier
                                    .placeholder(
                                        visible = true,
                                        color = SurfaceTheme.placeholder_primary.color,
                                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                                        shape = RoundedCornerShape(15.dp)
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(30.dp))
                Column {
                    @Composable
                    fun ProfileItem(text: String) {
                        Column(
                            Modifier.padding(vertical = space / 4)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        SurfaceTheme.foreground.color,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(text = text, color = SurfaceTheme.text.color)
                            }
                        }
                    }

                    val profileCardButtonSize = 27

                    @Composable
                    fun ProfileCardButton(
                        text: String,
                        icon: Int
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    SurfaceTheme.foreground.color,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .size(profileCardButtonSize.vw),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(SurfaceTheme.text.color),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(12.vw).placeholder(true)
                                )
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    text = text,
                                    color = SurfaceTheme.text.color,
                                    modifier = Modifier.width(17.vw).placeholder(true),
                                    textAlign = TextAlign.Center,
                                    fontSize = 3.vw.sp
                                )
                            }
                        }
                    }

                    @Composable
                    fun RowSpaceEvenly(content: @Composable () -> Unit) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            content()
                        }
                    }

                    Divider(color = SurfaceTheme.divider.color)
                    Spacer(modifier = Modifier.size(20.dp))
                    RowSpaceEvenly {
                        ProfileCardButton("О себе", R.drawable.user)
                        ProfileCardButton("Оценки", R.drawable.book_alt)
                        ProfileCardButton("Уч. план", R.drawable.study_plan)
                    }
                    Spacer(modifier = Modifier.size(((100-profileCardButtonSize.toDouble()*3)/4).vw))
                    RowSpaceEvenly {
                        ProfileCardButton("Портфолио", R.drawable.trophy)
                        ProfileCardButton("Методички", R.drawable.book)
                        ProfileCardButton("Выйти", R.drawable.back)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileInfoPreview() {
    ProfileInfo(info = Data())
}