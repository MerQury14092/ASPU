//package com.merqury.aspu.ui.navfragments.timetable
//
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Picture
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.compose.ui.unit.dp
//import androidx.core.content.FileProvider
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleRegistry
//import com.merqury.aspu.appContext
//import com.merqury.aspu.ui.theme.SurfaceTheme
//import com.merqury.aspu.ui.theme.colorWithoutAnim
//import java.io.File
//import java.io.FileOutputStream
//
//
//fun renderComposableToBitmap(
//    composableContent: @Composable () -> Unit,
//    width: Int,
//    height: Int,
//    onBitmapReady: (Bitmap) -> Unit
//) {
//    val picture = remember { Picture() }
//    Column(
//        modifier = Modifier
//            .padding(padding)
//            .fillMaxSize()
//            .drawWithCache {
//                // Example that shows how to redirect rendering to an Android Picture and then
//                // draw the picture into the original destination
//                val width = this.size.width.toInt()
//                val height = this.size.height.toInt()
//                onDrawWithContent {
//                    val pictureCanvas =
//                        androidx.compose.ui.graphics.Canvas(
//                            picture.beginRecording(
//                                width,
//                                height
//                            )
//                        )
//                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
//                        this@onDrawWithContent.drawContent()
//                    }
//                    picture.endRecording()
//
//                    drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
//                }
//            }
//    ) {
//        ScreenContentToCapture()
//    }
//}
//
//
//fun shareTimetable() {
//    renderComposableToBitmap({
//        Box(modifier = Modifier
//            .padding(10.dp)
//            .background(SurfaceTheme.background.colorWithoutAnim, RoundedCornerShape(10.dp))){
//            Text(text = "Hello")
//        }
//    }, 100, 100) {
//        shareBitmapImage(it)
//    }
//}
//
//private fun shareBitmapImage(bitmap: Bitmap) {
//    val file = File(appContext?.cacheDir, "images/timetable_${System.currentTimeMillis()}.png").apply {
//        parentFile?.mkdirs()
//    }
//
//    FileOutputStream(file).use { out ->
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//    }
//
//    val uri = FileProvider.getUriForFile(
//        appContext!!,
//        "${appContext?.packageName}",
//        file
//    )
//
//    val shareIntent = Intent(Intent.ACTION_SEND).apply {
//        type = "image/png"
//        putExtra(Intent.EXTRA_STREAM, uri)
//        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//    }
//
//    appContext?.startActivity(Intent.createChooser(shareIntent, "Поделиться расписанием"))
//}