package com.merqury.aspu.ui.navfragments.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.enums.TimetableDisciplineType
import com.merqury.aspu.ui.navfragments.settings.settingsPreferences
import org.json.JSONObject

@Composable
@Preview(showBackground = true)
fun TimetableItemPreview() {
    Box(modifier = Modifier.background(Color.Red)) {
        TimetableItem(
            discipline = JSONObject(
                "{\n" +
                        "            \"time\": \"11:40-13:10\",\n" +
                        "            \"name\": \"Методика обучения математике в начальной школе\",\n" +
                        "            \"teacherName\": \"Катуржевская О.В.\",\n" +
                        "            \"audienceId\": \"14\",\n" +
                        "            \"subgroup\": 0,\n" +
                        "            \"type\": \"lec\",\n" +
                        "            \"groupName\": \"ВН-НиР-3-1\",\n" +
                        "            \"distant\": true\n" +
                        "        }"
            )
        )
    }
}

@Composable
fun TimetableItem(discipline: JSONObject) {
    val type = TimetableDisciplineType.valueOf(discipline.getString("type"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(15.dp),
                spotColor = Color.Black
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(type.colorInt)
        )
    ) {
        Box(modifier = Modifier.padding(5.dp)) {
            Column {
                Text(
                    discipline.getString("time"),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    discipline.getString("name"),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Text(discipline.getString("teacherName"))
                Row {
                    Text("Аудитория: ")
                    Text(
                        text = discipline.getString("audienceId"),
                        fontWeight = FontWeight.Bold
                    )
                }
                if( !settingsPreferences.getBoolean("filtration_on", true) ||
                        (settingsPreferences.getInt("selected_subgroup", 0) == 0
                    || settingsPreferences.getString("timetable_id", "ВМ-ИВТ-2-1") != selectedId.value)) {
                    if(discipline.getInt("subgroup") != 0)
                        Text("Подгруппа: ${discipline.getString("subgroup")}")
                }
                Box {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            getDisciplineNumberByTime(discipline.getString("time")).toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(discipline.getString("groupName"))
                        if (type != TimetableDisciplineType.none)
                            Text(type.localizedName)
                    }
                }
                if (discipline.getBoolean("distant")) {
                    Divider()
                    Text(
                        text = "ДИСТАНЦИОННО",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun getDisciplineNumberByTime(time: String): Int {
    return when (time) {
        "8:00-9:30" -> 1
        "9:40-11:10" -> 2
        "11:40-13:10" -> 3
        "13:30-15:00" -> 4
        "15:10-16:40" -> 5
        "16:50-18:20" -> 6
        "18:30-20:00" -> 7
        else -> 8
    }
}