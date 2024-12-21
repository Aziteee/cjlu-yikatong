package cn.azite.cjlu_yikatong.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Modifier

@Composable
fun Base64Image(modifier: Modifier = Modifier, base64Data: String, contentDescription: String? = "") {
    val bitmap = remember(base64Data) { decodeBase64ToBitmap(base64Data) }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}

fun decodeBase64ToBitmap(base64Data: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Data.replace("data:image/jpeg;base64,", ""), Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}