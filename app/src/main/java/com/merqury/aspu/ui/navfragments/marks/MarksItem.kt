package com.merqury.aspu.ui.navfragments.marks

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.services.marks.models.MarksResponse
import com.merqury.aspu.services.studyplan.PlanElement

@Composable
fun MarksItem(
    marks: MarksResponse.ZachBook
) {
    val markColor = when (marks.mark) {
        "Зачет", "Отл" -> Color(0xFFAFE6C7)
        "Хор" -> Color(0xFFB4D4EC)
        "Удовл" -> Color(0xFFE7BFA2)
        "Плохо", "Незачет" -> Color(0xFFD37E81)
        else -> Color(0xFFB1B0B0)
    }

    Box(modifier = Modifier.padding(5.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(markColor, shape = RoundedCornerShape(15.dp))
                .padding(5.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = marks.dis ?: "N/A",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(5.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = fullMarkName(marks.mark!!), fontWeight = FontWeight.Bold)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = if (marks.hours != null) "${marks.hours} ч." else "")
                Text(text = marks.date!!)
            }

            Text(text = marks.teacherName!!, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun MarksItem(
    planElement: PlanElement
) {
    val markColor = Color(0xFFB1B0B0)

    Box(modifier = Modifier.padding(5.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(markColor, shape = RoundedCornerShape(15.dp))
                .padding(5.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = planElement.name,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
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
                    Text(text = if (planElement.hours != 0) "${planElement.hours} ч." else "")
                    Text(text = "")
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Нет информации", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun fullMarkName(mark: String): String {
    return when (mark) {
        "Отл" -> "Отлично"
        "Хор" -> "Хорошо"
        "Удовл" -> "Удовлетворительно"
        "Зачет" -> "Зачёт"
        else -> "Нет оценки"
    }
}