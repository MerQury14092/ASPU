package com.merqury.aspu.ui.navfragments.other

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.merqury.aspu.R
import com.merqury.aspu.appContext
import com.merqury.aspu.services.FileOpener
import com.merqury.aspu.ui.aspuButtonLoading
import com.merqury.aspu.ui.openInBrowser
import com.merqury.aspu.ui.theme.SurfaceTheme
import java.util.Stack

class WebViewActivity : ComponentActivity() {
    companion object {
        var url = ObservableString("")
    }

    private val urlHistory: Stack<String> = Stack()
    private var urlReturned = false

    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContent {
            val loading = remember {
                mutableStateOf(false)
            }
            var currentUrl = ""
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.06f)
                        .background(com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.foreground]!!)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                finish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.button]!!
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.text]!!),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Button(
                            onClick = {
                                finish()
                                val urlParts = currentUrl.split("://")
                                openInBrowser(urlParts[1], urlParts[0])
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.button]!!
                            )
                        ) {
                            Text(
                                text = "Открыть в браузере",
                                color = com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.text]!!
                            )
                            Image(
                                painter = painterResource(id = R.drawable.browser),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.text]!!),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.divider]!!)
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    AndroidView(factory = {
                        val webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                if (!urlReturned)
                                    urlHistory.push(currentUrl)
                                urlReturned = false
                                if (urlHistory.isEmpty())
                                    finish()
                                view!!.loadUrl("javascript:window.android.onUrlChange(window.location.href);")
                                currentUrl = url!!
                            }

                            override fun onPageCommitVisible(view: WebView?, url: String?) {
                                super.onPageCommitVisible(view, url)
                                loading.value = false
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                if (request!!.isForMainFrame) {
                                    aspuButtonLoading.value = false
                                    loading.value = true
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        WebView(it).apply {
                            this.webViewClient = webViewClient
                            settings.javaScriptEnabled = true
                            if(WebViewActivity.url.value.contains("it-institut"))
                                settings.builtInZoomControls = true
                            loadUrl(WebViewActivity.url.value)
                            WebViewActivity.url.subscribe { newUrl ->
                                post {
                                    loadUrl(newUrl)
                                }
                            }
                            setDownloadListener { url, _, _, _, _ ->
                                FileOpener.open(appContext!!, url)
                                loading.value = false
                            }
                        }
                    })
                    PullRefreshIndicator(refreshing = loading.value, state = rememberPullRefreshState(
                        refreshing = loading.value,
                        onRefresh = { /*TODO*/ }),
                        backgroundColor = com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.foreground]!!,
                        contentColor = com.merqury.aspu.ui.theme.theme.value[SurfaceTheme.text]!!
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (urlHistory.isEmpty())
            finish()
        else {
            url.value = urlHistory.pop()
            urlReturned = true
        }
    }
}