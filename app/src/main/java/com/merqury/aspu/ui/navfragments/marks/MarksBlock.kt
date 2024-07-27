package com.merqury.aspu.ui.navfragments.marks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color

@Composable
fun MarksBlock(
    type: ControlType,
    semester: Int
) {
    Box(modifier = Modifier.padding(10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.foreground.color, RoundedCornerShape(10.dp))
        ) {
            ThemeText(
                text = type.label,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.size(5.dp))
            if (marksContent == null) {
                MarksItemPlaceholder()
                MarksItemPlaceholder()
                MarksItemPlaceholder()
            } else {
                val studyPlanForSemester =
                    plan.filter { it.semester == semester && it.control.type == type }
                var marks = marksContent!!.data!!.zachBook!!

                studyPlanForSemester.onEach { control ->
                    if(marks.any {
                        it.dis!! == control.name
                                && it.sem!!.toInt() == control.semester
                                && it.controlForm!!.lowercase() != "курсовая работа"
                    })
                        MarksItem(marks = marks.first {
                            it.dis!! == control.name
                                    && it.sem!!.toInt() == control.semester
                                    && it.controlForm!!.lowercase() != "курсовая работа"
                        })
                    else
                        MarksItem(control)
                }
            }
        }
    }
}