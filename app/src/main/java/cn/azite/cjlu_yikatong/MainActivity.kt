package cn.azite.cjlu_yikatong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.azite.cjlu_yikatong.ui.theme.CjluyikatongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CjluyikatongTheme {
                Application()
            }
        }
    }
}
