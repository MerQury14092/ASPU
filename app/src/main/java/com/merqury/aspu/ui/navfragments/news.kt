package com.merqury.aspu.ui.navfragments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun NewsItemPreview(){
    NewsItem(
        title = "НА «РАЗГОВОРАХ В ВАЖНОМ» ОБСУДИЛИ ДОСТИЖЕНИЯ СТРАНЫ В РАЗЛИЧНЫХ ОТРАСЛЯХ",
        date = "19.02.2024",
        imageUrl = ""
    )
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview(){
    NewsScreen()
}

@Composable
fun NewsScreen(){
    Column {
        Box (modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.07f)
            .background(Color.Gray)) {
            Row {
                Text("1 из 1")
                Button(onClick = {
                    Log.d("DebugLog", "clicked 'next'")
                }) {
                    Text(text = "Далее")
                }
            }
        }
        NewsContent()
    }
}

@Composable
fun NewsContent(){
    LazyColumn(){
        items(count = 100) {
            NewsItem(
                title = "Новость $it",
                date = "09.09.2023",
                imageUrl = "https://sun9-48.userapi.com/impg/tzyETCh8L072f4kCQDoHuE_-pRkENavv9V8hZw/clU0g_YAfcs.jpg?size=807x552&quality=95&sign=158abe1c25ee60642469fd6409be1e1b&c_uniq_tag=4a53Sbi77-9zhC7yHlJ539T7VSj5hf2S0EPJH5NkDRc&type=album"
            )
        }
    }
}

@Composable
fun NewsItem(title: String, date: String, imageUrl: String){
    Card (shape = RoundedCornerShape(15.dp), modifier = Modifier.padding(5.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 25.dp
                    ),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic
            )
            AsyncImage(
                model = imageUrl,
                contentDescription = "image url",
                placeholder = painterResource(
                    id = R.drawable.news_image_placeholder
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                date,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp), textAlign = TextAlign.End)
        }
    }
}