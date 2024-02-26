package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.merqury.aspu.R

@Preview(showBackground = true)
@Composable
fun NewsItemPreview() {
    NewsItem(
        title = "НОВОСТЬ 1",
        date = "19.02.6666",
        imageUrl = ""
    )
}
@Composable
fun NewsItem(title: String, date: String, imageUrl: String) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(8.dp)
            .shadow(
                elevation = 10.dp,
                spotColor = Color.Black,
                shape = RoundedCornerShape(15.dp)
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
                        top = 25.dp
                    ),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic
            )
            Text(
                date,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp), textAlign = TextAlign.End
            )
        }
    }
}
