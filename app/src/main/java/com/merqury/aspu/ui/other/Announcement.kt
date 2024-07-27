package com.merqury.aspu.ui.other

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.services.appconfig.AppConfig
import com.merqury.aspu.services.appconfig.models.Announcement
import com.merqury.aspu.services.appconfig.models.AnnouncementType
import com.merqury.aspu.ui.HtmlText
import com.merqury.aspu.ui.bounceClick
import com.merqury.aspu.ui.openInBrowser
import com.merqury.aspu.ui.showSimpleModalWindow
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.ThemeText
import com.merqury.aspu.ui.theme.color
import kotlin.system.exitProcess


@Composable
@Preview
private fun AnnouncementPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Announcement(
            announcement = Announcement(
                0,
                "all",
                true,
                AnnouncementType.blocking,
                "Данная версия приложения больше не поддерживается",
                "<h2>A Description List</h2>\n" +
                        "\n" +
                        "<dl>\n" +
                        "  <dt>Coffee</dt>\n" +
                        "  <dd>- black hot drink</dd>\n" +
                        "  <dt>Milk</dt>\n" +
                        "  <dd>- white cold drink</dd>\n" +
                        "</dl>"
            ),
            {}, {}
        )
    }
}

fun showAnnouncement(announcement: Announcement) {
    showSimpleModalWindow(
        closeable = false
    ) {
        Announcement(announcement = announcement, {
            openInBrowser("https", "www.rustore.ru/catalog/app/com.merqury.aspu")
        }) {
            if(announcement.type == AnnouncementType.simple){
                AppConfig.saveAnnouncement(announcement.id)
            }
            it.value = false
        }
    }
}

@Composable
private fun Announcement(announcement: Announcement, update: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(.97f)
            .verticalScroll(rememberScrollState())
            .background(SurfaceTheme.background.color)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.agpu_logo),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            ThemeText(text = announcement.title, fontSize = 25.sp, textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTheme.foreground.color, RoundedCornerShape(5.dp))
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            HtmlText(
                text = announcement.body,
                color = SurfaceTheme.text.color,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            ) {
            if (!announcement.updateRequest)
                Spacer(modifier = Modifier.size(1.dp))
            if(announcement.type == AnnouncementType.blocking)
                Box(modifier = Modifier.bounceClick {
                    exitProcess(0)
                }){
                    Box(
                        modifier = Modifier
                            .background(SurfaceTheme.button.color, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        ThemeText(text = "Выйти")
                    }
                }
            if (announcement.updateRequest && announcement.type == AnnouncementType.blocking)
                Spacer(modifier = Modifier.size(1.dp))
            if (announcement.updateRequest)
                Box(modifier = Modifier.bounceClick {
                    update()
                }){
                    Box(
                        modifier = Modifier
                            .background(SurfaceTheme.button.color, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        ThemeText(text = "Обновить")
                    }
                }
            if (announcement.type != AnnouncementType.blocking)
                Box(modifier = Modifier.bounceClick {
                    onClose()
                }){
                    Box(
                        modifier = Modifier
                            .background(SurfaceTheme.button.color, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                    ) {

                        ThemeText(
                            text = if (announcement.type == AnnouncementType.simple)
                                "Больше не показывать"
                            else "Хорошо"
                        )
                    }
                }
        }
    }
}