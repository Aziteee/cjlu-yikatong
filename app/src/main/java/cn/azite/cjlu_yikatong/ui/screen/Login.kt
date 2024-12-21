package cn.azite.cjlu_yikatong.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import cn.azite.cjlu_yikatong.R
import cn.azite.cjlu_yikatong.component.LoadingDialog
import java.net.URLEncoder

const val COOKIE_NAME = "qywx.cjlu.edu.cn.80.Token"

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {  },
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
        var webView: WebView? by remember { mutableStateOf(null) }

        var username by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }
        var passwordVisible by remember { mutableStateOf(false) }

        var loading by remember { mutableStateOf(false) }

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "登录", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "中国计量大学统一身份认证", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("学号/工号") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        ImageVector.vectorResource(id = R.drawable.ic_visibility)
                    else
                        ImageVector.vectorResource(id = R.drawable.ic_visibility_off)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.width(100.dp).align(Alignment.CenterHorizontally),
                onClick = {
                    if (webView != null) {
                        loading = true
                        login(username.text, password.text, webView!!) { message ->
                            loading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("登录")
            }
        }

        if (loading) {
            LoadingDialog()
        }

        val serviceUrl = "https://qywx.cjlu.edu.cn/Pages/Default/index.html"
        val authUrl = "https://authserver.cjlu.edu.cn/authserver/login?service=" + URLEncoder.encode(serviceUrl, "UTF-8")
        val thirdServerAuthUrl = "https://authserver.cjlu.edu.cn/authserver/login?service=http%3a%2f%2fqywx.cjlu.edu.cn%2fpages%2fThirdServer%2fThirdLogon.aspx"

        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        AndroidView(factory = {
            WebView(it).apply {
                visibility = View.GONE
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
                        view.evaluateJavascript("""
                            (function() {
                                const iframe = document.getElementsByTagName("iframe")[0];
                                if (iframe) {
                                  if (document.cookie.includes("$COOKIE_NAME")) {
                                    return document.cookie;
                                  } else {
                                    iframe.src = "$thirdServerAuthUrl";
                                    iframe.onload = () => location.reload();
                                  }
                                }
                                return "";
                            })()
                        """.trimIndent()
                        ) { result ->
                            if (result.contains(COOKIE_NAME)) {
                                sharedPreferences.edit()
                                    .putString("cookie", result.replace("\"", "")).apply()
                                navController.popBackStack()
                            }
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

                webView = this
            }
        }, update = {
            it.loadUrl(authUrl)
        })
    }
}

fun login(username: String, password: String, webView: WebView, onError: (message: String) -> Unit) {
    webView.evaluateJavascript("""
        document.getElementById("username").value="$username";
        document.getElementById("password").value="$password";
        document.getElementById("login_submit").click();
    """.trimIndent()) {
        Handler(Looper.getMainLooper()).postDelayed({
            webView.evaluateJavascript("""
                    (function() {
                        const alertModal = document.getElementById("alert-box");
                        if (alertModal) {
                          return "message:" + alertModal.querySelector("div").textContent;
                        }
                        return "";
                    })()
                """.trimIndent()
            ) { result ->
                if (result.contains("message:")) {
                    val message = result.replace("message:", "").replace("\"", "")
                    onError(message)
                }
            }
        }, 1000)
    }
}
