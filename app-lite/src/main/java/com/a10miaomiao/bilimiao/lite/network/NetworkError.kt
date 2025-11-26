package com.a10miaomiao.bilimiao.lite.network

/**
 * 网络错误信息
 */
data class NetworkError(
    val userFriendlyMessage: String,
    val isRetryable: Boolean = true,
    val errorCode: String? = null,
    val originalException: Throwable? = null
)