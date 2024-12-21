package cn.azite.cjlu_yikatong.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(onDismissRequest: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            val context = LocalContext.current
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

            Column(modifier = Modifier.padding(16.dp).width(320.dp)) {
                Text(
                    text = "量大一卡通",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "v${packageInfo.versionName} (${packageInfo.longVersionCode})",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "本项目不会收集您的账号和密码，您的账号密码将直接发送到中国计量大学统一认证服务。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Spacer(modifier = Modifier.height(24.dp))
                GithubHyperlinkedText()
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("确定")
                }
            }
        }
    }
}

@Composable
fun GithubHyperlinkedText() {
    val context = LocalContext.current

    val annotatedString = buildAnnotatedString {
        append("在 ")
        pushStringAnnotation(tag = "URL", annotation = "https://github.com/Aziteee/cjlu-yikatong")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append("Github")
        }
        pop()
        append(" 上查看源代码")
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        },
        style = MaterialTheme.typography.bodyMedium
    )
}
