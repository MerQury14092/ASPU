package com.merqury.aspu.ui.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.R
import com.merqury.aspu.services.file.models.FileModel
import com.merqury.aspu.ui.round
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.color
import com.merqury.aspu.ui.vw

enum class FileExtension(
    val imageId: Int
) {
    PDF(R.drawable.pdf),
    WORD(R.drawable.word),
    EXCEL(R.drawable.excel),
    POWERPOINT(R.drawable.powerpoint),
    JPG(R.drawable.jpg),
    UNKNOWN(R.drawable.jpg)
}

fun prettySize(size: Long): String {
    val res: Double
    val ext: String
    if (size > 1_000_000) {
        res = (size / 1_000_000.0).round(2)
        ext = "MB"
    } else if (size > 1_000) {
        res = (size / 1_000.0).round(2)
        ext = "KB"
    } else {
        res = size.toDouble()
        ext = "B"
    }
    return "$res $ext"
}

@Composable
fun FileView(file: FileModel, onClick: () -> Unit = {}) {
    val extension = when (file.fileName.split(".").last()) {
        "pdf" -> FileExtension.PDF
        "jpeg" -> FileExtension.JPG
        "jpg" -> FileExtension.JPG
        "doc" -> FileExtension.WORD
        "docx" -> FileExtension.WORD
        "ppt" -> FileExtension.POWERPOINT
        "pptx" -> FileExtension.POWERPOINT
        "xlsx" -> FileExtension.EXCEL
        else -> FileExtension.UNKNOWN
    }
    val displayName =
        file.fileName.substring(
            0,
            file.fileName.length - file.fileName.split(".").last().length - 1
        )
    Box(
        modifier = Modifier
            .background(SurfaceTheme.foreground.color, RoundedCornerShape(15.dp))
            .clickable {
                onClick()
            }) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = extension.imageId),
                contentDescription = null,
                Modifier.size(40.dp)
            )
            Text(
                text = displayName,
                color = SurfaceTheme.text.color,
                fontSize = 20.sp,
                modifier = Modifier.width(50.vw)
            )
            Text(text = prettySize(file.size), color = SurfaceTheme.text.color, fontSize = 20.sp)
        }
    }
}