package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.R
import com.merqury.aspu.ui.navfragments.timetable.prettyDate
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme

@Composable
fun NewsItem(title: String, date: String, imageUrl: String, id: Int) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(vertical = 5.dp)
//            .shadow(
//                elevation = 10.dp,
//                spotColor = Color.Black,
//                shape = RoundedCornerShape(15.dp)
//            )
            .clickable {
                clickedArticleId.intValue = id
                showArticleView.value = true
            },
        colors = CardDefaults.cardColors(
            containerColor = theme.value[SurfaceTheme.foreground]!!
        )
    ) {
        Column(Modifier.fillMaxWidth()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "image url",
                placeholder = painterResource(
                    id = R.drawable.news_image_placeholder
                ),
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 25.dp
                    ),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                color = theme.value[SurfaceTheme.text]!!
            )
            Text(
                prettyDate(date),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp, bottom = 5.dp), textAlign = TextAlign.End,
                color = theme.value[SurfaceTheme.text]!!
            )
        }
    }
}

@Composable
fun NewsItemLoadingPlaceholder() {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(vertical = 5.dp)
//            .shadow(
//                elevation = 10.dp,
//                spotColor = Color.Black,
//                shape = RoundedCornerShape(15.dp)
//            )
        , colors = CardDefaults.cardColors(
            containerColor = theme.value[SurfaceTheme.foreground]!!
        )
    ) {
        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(
                    id = R.drawable.news_image_placeholder
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .placeholder(
                        visible = true,
                        color = theme.value[SurfaceTheme.placeholder_primary]!!,
                        highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    "В этой новости рассказывается что то очень важное",
                    modifier = Modifier
                        .padding(
                            bottom = 25.dp,
                            top = 5.dp
                        )
                        .placeholder(
                            visible = true,
                            color = theme.value[SurfaceTheme.placeholder_primary]!!,
                            highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = theme.value[SurfaceTheme.text]!!
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Сегодня",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .placeholder(
                            visible = true,
                            color = theme.value[SurfaceTheme.placeholder_primary]!!,
                            highlight = PlaceholderHighlight.shimmer(theme.value[SurfaceTheme.placeholder_secondary]!!),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(end = 10.dp, bottom = 5.dp), textAlign = TextAlign.End,
                    color = theme.value[SurfaceTheme.text]!!
                )
            }
        }
    }
}
