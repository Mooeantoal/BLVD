package com.a10miaomiao.bilimiao.lite.network

import cn.a10miaomiao.bilimiao.comm.network.MiaoHttp
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

object NetworkExceptionHandler {
    
    fun handleException(throwable: Throwable): NetworkError {
        return when (throwable) {
            is MiaoHttp.NetworkException -> {
                when (throwable.cause) {
                    is UnknownHostException -> NetworkError.NO_INTERNET
                    is SocketTimeoutException -> NetworkError.TIMEOUT
                    is SSLException -> NetworkError.SSL_ERROR
                    is IOException -> NetworkError.IO_ERROR
                    else -> NetworkError.NETWORK_ERROR
                }
            }
            is MiaoHttp.ApiException -> {
                when (throwable.code) {
                    401 -> NetworkError.UNAUTHORIZED
                    403 -> NetworkError.FORBIDDEN
                    404 -> NetworkError.NOT_FOUND
                    429 -> NetworkError.RATE_LIMIT
                    in 500..599 -> NetworkError.SERVER_ERROR
                    else -> NetworkError.API_ERROR
                }
            }
            is UnknownHostException -> NetworkError.NO_INTERNET
            is SocketTimeoutException -> NetworkError.TIMEOUT
            is SSLException -> NetworkError.SSL_ERROR
            is IOException -> NetworkError.IO_ERROR
            else -> NetworkError.UNKNOWN_ERROR
        }
    }
}

enum class NetworkError(
    val message: String,
    val userFriendlyMessage: String,
    val isRetryable: Boolean = true
) {
    NO_INTERNET(
        message = "No internet connection",
        userFriendlyMessage = "网络连接失败，请检查网络设置",
        isRetryable = true
    ),
    
    TIMEOUT(
        message = "Request timeout",
        userFriendlyMessage = "请求超时，请稍后重试",
        isRetryable = true
    ),
    
    SSL_ERROR(
        message = "SSL error",
        userFriendlyMessage = "安全连接失败，请检查网络环境",
        isRetryable = true
    ),
    
    IO_ERROR(
        message = "IO error",
        userFriendlyMessage = "网络传输错误，请重试",
        isRetryable = true
    ),
    
    NETWORK_ERROR(
        message = "Network error",
        userFriendlyMessage = "网络错误，请检查网络连接",
        isRetryable = true
    ),
    
    API_ERROR(
        message = "API error",
        userFriendlyMessage = "服务器响应错误，请稍后重试",
        isRetryable = true
    ),
    
    UNAUTHORIZED(
        message = "Unauthorized",
        userFriendlyMessage = "身份验证失败，请重新登录",
        isRetryable = false
    ),
    
    FORBIDDEN(
        message = "Forbidden",
        userFriendlyMessage = "访问被拒绝，请检查权限",
        isRetryable = false
    ),
    
    NOT_FOUND(
        message = "Not found",
        userFriendlyMessage = "请求的资源不存在",
        isRetryable = false
    ),
    
    RATE_LIMIT(
        message = "Rate limit exceeded",
        userFriendlyMessage = "请求过于频繁，请稍后重试",
        isRetryable = true
    ),
    
    SERVER_ERROR(
        message = "Server error",
        userFriendlyMessage = "服务器内部错误，请稍后重试",
        isRetryable = true
    ),
    
    UNKNOWN_ERROR(
        message = "Unknown error",
        userFriendlyMessage = "未知错误，请重试",
        isRetryable = true
    )
}