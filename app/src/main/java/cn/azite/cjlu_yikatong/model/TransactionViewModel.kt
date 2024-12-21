package cn.azite.cjlu_yikatong.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cn.azite.cjlu_yikatong.ui.screen.dateFormatter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TransactionViewModel : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var startTime by mutableStateOf(
        LocalDateTime.now().minusWeeks(1).format(
            dateFormatter
        ))
        private set
    var endTime by mutableStateOf(LocalDateTime.now().format(dateFormatter))
        private set

    var transactionList = mutableStateListOf<Transaction>()

    fun getTransactionList(cookie: String) {
        loading = true

        val client = OkHttpClient()

        val requestBody = "{\"sBeginDate\":\"$startTime\",\"sEndDate\":\"$endTime\"}"
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("https://qywx.cjlu.edu.cn/Pages/QRCode/ConQRCodePayBooks.aspx/GetAccPayBook")
            .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 > MicroMessenger/6.0.2.56_r958800.520 NetType/WIFI Edg/113.0.0.0")
            .addHeader("Cookie", cookie.trim())
            .post(requestBody)
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

                        val jsonObject = responseBody?.let { it1 -> JSONObject(it1) }
                        val jsonArray = JSONArray(jsonObject?.getString("d"))

                        transactionList.clear()

                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            val transaction = Transaction(
                                dealer = item.getString("DEALERNAME"),
                                time = item.getString("DEALTIME"),
                                amount = item.getString("MONDEAL").toFloat()
                            )
                            transactionList.add(transaction)
                        }

                        loading = false
                    }
                }
            }
        })
    }

    fun setTime(timeRange: Pair<Long, Long>) {
        startTime = timestampToDateString(timeRange.first)
        endTime = timestampToDateString(timeRange.second)
    }
}

fun timestampToDateString(timestamp: Long): String {
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    return localDateTime.format(dateFormatter)
}
