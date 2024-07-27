package com.merqury.aspu.ui.navfragments.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color

@Composable
fun CourseItem(item: ExamCourse, isMyCourse: Boolean) {
    Box(modifier = Modifier
        .padding(5.dp)
        .bounceClick {
            if (!isMyCourse) {
                showCourseAuthModalWindow(course = item){
                    showCourseActionsModalWindow(item)
                }
            } else
                showCourseActionsModalWindow(item)
        }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.button.color, shape = RoundedCornerShape(15.dp))
                .padding(5.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThemeText(text = item.name, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CourseItemPlaceholder() {
    Box(modifier = Modifier.padding(5.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.foreground.color, shape = RoundedCornerShape(15.dp))
                .padding(5.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThemeText(
                text = "Экзамен по разработке Андроид",
                textAlign = TextAlign.Center,
                modifier = Modifier.placeholder(
                    visible = true,
                    color = SurfaceTheme.placeholder_primary.color,
                    highlight = PlaceholderHighlight.shimmer(SurfaceTheme.placeholder_secondary.color),
                    shape = RoundedCornerShape(15.dp)
                )
            )
        }
    }
}