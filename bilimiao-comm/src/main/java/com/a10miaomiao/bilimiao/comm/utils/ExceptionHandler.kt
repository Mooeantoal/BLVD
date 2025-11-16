package com.a10miaomiao.bilimiao.comm.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import java.io.IOException

/**
 * 统一的异常处理工具类
 * 提供标准化的异常处理策略和错误分类
 */
object ExceptionHandler {
    
    private const val TAG = "ExceptionHandler"
    
    /**
     * 异常分类
     */
    sealed class AppException(
        message: String,
        cause: Throwable? = null
    ) : Exception(message, cause) {
        
        /**
         * 网络异常
         */
        data class NetworkException(
            val url: String? = null,
            val statusCode: Int? = null,
            cause: Throwable? = null
        ) : AppException(
            message = "Network error: ${url ?: "unknown"}${statusCode?.let { " (status: $it)" } ?: ""}",
            cause = cause
        )
        
        /**
         * 数据解析异常
         */
        data class ParseException(
            val dataType: String,
            val rawData: String? = null,
            cause: Throwable? = null
        ) : AppException(
            message = "Parse error for $dataType",
            cause = cause
        )
        
        /**
         * 业务逻辑异常
         */
        data class BusinessException(
            val code: Int,
            val message: String
        ) : AppException(message)
        
        /**
         * 文件操作异常
         */
        data class FileException(
            val operation: String,
            val filePath: String,
            cause: Throwable? = null
        ) : AppException(
            message = "File error: $operation on $filePath",
            cause = cause
        )
    }
    
    /**
     * 安全地执行操作，提供默认返回值
     */
    inline fun <T> safeExecute(
        operation: String = "unknown",
        default: T,
        block: () -> T
    ): T {
        return try {
            block()
        } catch (e: Exception) {
            handleException(e, operation)
            default
        }
    }
    
    /**
     * 安全地执行可能抛出异常的操作，返回 Result<T>
     */
    inline fun <T> safeResult(
        operation: String = "unknown",
        block: () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            handleException(e, operation)
            Result.failure(e)
        }
    }
    
    /**
     * 安全地执行网络请求
     */
    suspend inline fun <T> safeNetworkCall(
        url: String,
        operation: String = "network call",
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: IOException) {
            val networkException = AppException.NetworkException(url, null, e)
            handleException(networkException, operation)
            Result.failure(networkException)
        } catch (e: Exception) {
            handleException(e, operation)
            Result.failure(e)
        }
    }
    
    /**
     * 统一异常处理
     */
    fun handleException(exception: Throwable, operation: String = "unknown") {
        when (exception) {
            is AppException -> {
                // 应用特定异常，按需处理
                when (exception) {
                    is AppException.NetworkException -> {
                        Log.w(TAG, "Network error in $operation: ${exception.message}")
                    }
                    is AppException.ParseException -> {
                        Log.e(TAG, "Parse error in $operation: ${exception.message}", exception)
                    }
                    is AppException.BusinessException -> {
                        Log.w(TAG, "Business error in $operation: ${exception.message}")
                    }
                    is AppException.FileException -> {
                        Log.e(TAG, "File error in $operation: ${exception.message}", exception)
                    }
                }
            }
            is IOException -> {
                Log.w(TAG, "IO error in $operation: ${exception.message}")
            }
            is IllegalStateException, is IllegalArgumentException -> {
                Log.e(TAG, "State/argument error in $operation: ${exception.message}", exception)
            }
            else -> {
                Log.e(TAG, "Unexpected error in $operation: ${exception.message}", exception)
            }
        }
        
        // 可以根据需要添加错误上报逻辑
        // reportErrorToAnalytics(exception, operation)
    }
    
    /**
     * 创建协程异常处理器
     */
    fun createCoroutineExceptionHandler(context: String = "unknown"): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            handleException(exception, "coroutine in $context")
        }
    }
    
    /**
     * 将通用异常转换为应用特定异常
     */
    fun wrapAsAppException(exception: Throwable, operation: String): AppException {
        return when (exception) {
            is IOException -> AppException.NetworkException(operation = operation, cause = exception)
            is IllegalStateException, is IllegalArgumentException -> 
                AppException.BusinessException(-1, "Invalid operation: $operation")
            else -> AppException.BusinessException(-1, "Unexpected error: ${exception.message ?: "unknown"}")
        }
    }
}