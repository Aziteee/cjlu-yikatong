package cn.azite.cjlu_yikatong.model

import androidx.compose.runtime.Immutable

@Immutable
data class Transaction(
    val dealer: String,
    val time: String,
    val amount: Float
)