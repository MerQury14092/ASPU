package com.merqury.aspu.ui.greetings

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.Volley
import com.merqury.aspu.MainActivity
import com.merqury.aspu.requestQueue
import com.merqury.aspu.services.misc.AppSettings
import com.merqury.aspu.ui.ColorizeAppBars
import com.merqury.aspu.ui.contentList
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
internal val pagerState = PagerState { 5 }
var greetingsPagerActivity: ComponentActivity? = null

class GreetingsPager : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        greetingsPagerActivity = this
        requestQueue = Volley.newRequestQueue(this)
        setContent {
            contentList.forEach {
                it()
            }
            ColorizeAppBars(window = window, SurfaceTheme.background.color)
            val coroutineScope = rememberCoroutineScope()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(.1f)
                    ) {
                        BottomBar(
                            pagerState = pagerState,
                            coroutineScope = coroutineScope
                        )
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(SurfaceTheme.background.color)
                ) {
                    HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->

                        when (page) {
                            0 -> UnofficialApplication()
                            1 -> SelectTheme()
                            2 -> SelectSubjectType()
                            3 -> SelectSubject()
                            4 -> InterfaceIntroShow {
                                AppSettings.firstLaunch = false
                                startActivity(Intent(this@GreetingsPager, MainActivity::class.java))
                                finish()
                            }
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTheme.background.color),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage - 1,
                                animationSpec = tween(600)
                            )
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceTheme.background.color
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "",
                        tint = SurfaceTheme.text.color,
                        modifier = Modifier.size(50.dp)
                    )
                }
            else Spacer(modifier = Modifier.size(1.dp))
            if (pagerState.currentPage in arrayOf(0, 1, 2, 3))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1,
                                animationSpec = tween(600)
                            )
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceTheme.background.color
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "",
                        tint = SurfaceTheme.text.color,
                        modifier = Modifier.size(50.dp)
                    )
                }
            else Spacer(modifier = Modifier.size(1.dp))
        }
        Row {
            repeat(pagerState.pageCount) { iteration ->
                Box(modifier = Modifier.padding(3.dp)) {
                    Box(
                        modifier = Modifier
                            .height(10.dp)
                            .width(
                                animateDpAsState(
                                    targetValue = if (iteration == pagerState.targetPage)
                                        20.dp
                                    else
                                        10.dp,
                                    label = "",
                                    animationSpec = tween(200)
                                ).value
                            )
                            .background(
                                if (iteration == pagerState.targetPage)
                                    SurfaceTheme.text.color
                                else
                                    SurfaceTheme.disable.color,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}