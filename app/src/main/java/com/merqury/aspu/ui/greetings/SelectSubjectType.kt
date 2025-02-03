package com.merqury.aspu.ui.greetings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.AutoSizeThemeText
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color

@Composable
fun SelectSubjectType() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.aspu), contentDescription = null)
            Spacer(modifier = Modifier.size(50.dp))
            ThemeText(
                text = "Кто вы?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.size(20.dp))
            Row(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .bounceClick {
                            if(AppSettings.eiosLogged)
                                return@bounceClick
                            AppSettings.whoIsUser = "student"
                            AppSettings.timetableIdOwner = "GROUP"
                        }
                        .padding(10.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                SurfaceTheme.button.color,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(.67f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.student_cap),
                                contentDescription = "",
                                tint = SurfaceTheme.text.color,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            AutoSizeThemeText(text = "Студент      ")
                        }
                        if (AppSettings.whoIsUser == "student") {
                            Image(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    SurfaceTheme.text.color
                                ),
                                modifier = Modifier.size(15.dp)
                            )
                        } else
                            Spacer(modifier = Modifier.size(15.dp))
                    }
                }
                Box(
                    modifier = Modifier
                        .bounceClick {
                            if(AppSettings.eiosLogged)
                                return@bounceClick
                            AppSettings.whoIsUser = "teacher"
                            AppSettings.timetableIdOwner = "TEACHER"
                        }
                        .padding(10.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                SurfaceTheme.button.color,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(.8f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.teacher),
                                contentDescription = "",
                                tint = SurfaceTheme.text.color,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            AutoSizeThemeText(text = "Преподаватель")
                        }
                        if (AppSettings.whoIsUser == "teacher") {
                            Image(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    SurfaceTheme.text.color
                                ),
                                modifier = Modifier.size(15.dp)
                            )
                        } else
                            Spacer(modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}