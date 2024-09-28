package com.merqury.aspu.ui.screens.marks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color

@Composable
fun MarksItemPlaceholder(){
    Box(modifier = Modifier.padding(5.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.button.color, shape = RoundedCornerShape(15.dp))
                .padding(5.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Какая-та дисциплина",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.placeholder(
                    visible = true,
                    color = SurfaceTheme.placeholder_primary.color,
                    highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                    shape = RoundedCornerShape(15.dp)
                ),
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(5.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(text = "144 ч.", Modifier.placeholder(
                        visible = true,
                        color = SurfaceTheme.placeholder_primary.color,
                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                        shape = RoundedCornerShape(15.dp)
                    ))
                    Text(text = "24.04.2024", Modifier.placeholder(
                        visible = true,
                        color = SurfaceTheme.placeholder_primary.color,
                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                        shape = RoundedCornerShape(15.dp)
                    ))
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Отлично", fontWeight = FontWeight.Bold, modifier = Modifier.placeholder(
                        visible = true,
                        color = SurfaceTheme.placeholder_primary.color,
                        highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                        shape = RoundedCornerShape(15.dp)
                    ))
                }
            }
            Text(text = "Лапшин Н.А.", textAlign = TextAlign.Center, modifier = Modifier.placeholder(
                visible = true,
                color = SurfaceTheme.placeholder_primary.color,
                highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                shape = RoundedCornerShape(15.dp)
            ))
        }
    }
}