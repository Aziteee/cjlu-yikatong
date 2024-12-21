package cn.azite.cjlu_yikatong.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import java.net.URLEncoder

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "登录") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") }
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val serviceUrl = "https://qywx.cjlu.edu.cn/Pages/Default/index.html"
            val authUrl = "https://authserver.cjlu.edu.cn/authserver/login?service=" + URLEncoder.encode(serviceUrl, "UTF-8")

            val context = LocalContext.current
            val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                            val uri = request.url
                            view.loadUrl(uri.toString())
                            return true
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            if (url == authUrl) {

                            } else {
                                view.evaluateJavascript("""
                                (function() {
                                    const iframe = document.getElementsByTagName("iframe")[0];
                                    if (iframe) {
                                      if (document.cookie.includes("qywx.cjlu.edu.cn.80.Token")) {
                                        return document.cookie;
                                      } else {
                                        iframe.src = "https://authserver.cjlu.edu.cn/authserver/login?service=http%3a%2f%2fqywx.cjlu.edu.cn%2fpages%2fThirdServer%2fThirdLogon.aspx";
                                        iframe.onload = () => location.reload();
                                      }
                                    }
                                    return "";
                                })()
                            """.trimIndent(), { result ->
                                    println("webview result: $result")
                                    if (result.contains("qywx.cjlu.edu.cn.80.Token")) {
                                        sharedPreferences.edit().putString("cookie", result.replace("\"", "")).apply()
                                        navController.popBackStack()
                                    }
                                })
                            }
                        }
                    }

                    settings.javaScriptEnabled = true
                    settings.allowContentAccess = true
                    settings.domStorageEnabled = true
                    settings.defaultTextEncodingName = "utf-8"
                    settings.builtInZoomControls = false
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.removeAllCookies(null)
                    cookieManager.flush()
                }
            }, update = {
                it.loadUrl(authUrl)
            })
        }
    }
}
