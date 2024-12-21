package cn.azite.cjlu_yikatong.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

class HomeViewModel: ViewModel() {
    var name by mutableStateOf("")
        private set
    var className by mutableStateOf("")
        private set
    var qrCodeString by mutableStateOf("")
        private set
    var balance by mutableStateOf("")
        private set

    var loading by mutableStateOf(true)
        private set

    var html by mutableStateOf("")
        private set

    fun getData(cookie: String) {
        loading = true
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://qywx.cjlu.edu.cn/Pages/QRCode/ConQRCodeU.aspx")
            .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 > MicroMessenger/6.0.2.56_r958800.520 NetType/WIFI Edg/113.0.0.0")
            .addHeader("Cookie", cookie.trim())
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        println("Response Body $response")
                    } else {
                        val responseBody = response.body?.string()
                        val document = responseBody?.let { it1 -> Jsoup.parse(it1) }

                        html = responseBody.toString()

                        name = document?.selectFirst("body > div:nth-child(2) > div:nth-child(1) > h3")?.text()
                            .toString()
                        className = document?.selectFirst("body > div:nth-child(2) > div:nth-child(1) > div:nth-child(3) > p")?.text()
                            .toString()
                        qrCodeString = document?.selectFirst("#DynamicQRimg")?.attr("src").toString()

                        val balanceString = document?.selectFirst("body > div:nth-child(2) > div:nth-child(1) > div:nth-child(6) > div:nth-child(2) > p:nth-child(3)")?.text()
                        balance = balanceString?.replace("剩余", "")?.trim().toString()

                        loading = false
                    }
                }
            }
        })
    }
}