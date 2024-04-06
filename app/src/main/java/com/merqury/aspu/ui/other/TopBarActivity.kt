package com.merqury.aspu.ui.other

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.merqury.aspu.appContext
import com.merqury.aspu.ui.contentList
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color


val activityContentList =
    ArrayList(listOf<@Composable (MutableState<@Composable () -> Unit>) -> Unit>())
val activityMap = HashMap<String, Activity>()

class TopBarActivity : ComponentActivity() {
    private var activityId: String = ""
    private val lastAppContext = appContext
    private var activityContent: @Composable (MutableState<@Composable () -> Unit>) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContent = activityContentList.last()
        activityContentList.removeLast()
        appContext = this
        activityId = intent.extras!!.getString("activityId")!!
        activityMap[activityId] = this
        setContent {
            contentList.forEach {
                it()
            }
            val topBarContent: MutableState<@Composable () -> Unit> = remember {
                mutableStateOf({})
            }
            Scaffold(
                topBar = {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(.06f)
                                .fillMaxWidth()
                                .background(SurfaceTheme.appBars.color)
                        ) { topBarContent.value() }
                        Divider(
                            color = SurfaceTheme.divider.color,
                            modifier = Modifier.height(2.dp)
                        )
                    }
                },
            ) {

                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .background(SurfaceTheme.background.color)
                ) {
                    activityContent(topBarContent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appContext = lastAppContext
        activityMap.remove(activityId)
    }
}

@Composable
fun ImageVectorButton(
    vector: ImageVector,
    color: Color = SurfaceTheme.text.color,
    onClick: () -> Unit
) {
    Image(
        imageVector = vector, contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxHeight()
            .clickable {
                onClick()
            },
        colorFilter = ColorFilter.tint(color),
    )
}