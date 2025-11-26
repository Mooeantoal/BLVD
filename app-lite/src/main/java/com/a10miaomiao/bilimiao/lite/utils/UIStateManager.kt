package com.a10miaomiao.bilimiao.lite.utils

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI状态管理器 - 统一管理加载、错误、空状态等
 */
class UIStateManager<T> {
    
    private val _uiState = MutableStateFlow<UIState<T>>(UIState.Idle)
    val uiState: StateFlow<UIState<T>> = _uiState
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _data = MutableStateFlow<T?>(null)
    val data: StateFlow<T?> = _data

    fun setLoading() {
        _isLoading.value = true
        _error.value = null
        _uiState.value = UIState.Loading
    }
    
    fun setData(data: T) {
        _isLoading.value = false
        _error.value = null
        _data.value = data
        _uiState.value = UIState.Success(data)
    }
    
    fun setError(errorMessage: String, data: T? = null) {
        _isLoading.value = false
        _error.value = errorMessage
        _data.value = data
        _uiState.value = UIState.Error(errorMessage, data)
    }
    
    fun setEmpty(emptyMessage: String = "暂无数据") {
        _isLoading.value = false
        _error.value = null
        _uiState.value = UIState.Empty(emptyMessage)
    }
    
    fun clearError() {
        _error.value = null
        if (_data.value != null) {
            _uiState.value = UIState.Success(_data.value!!)
        } else {
            _uiState.value = UIState.Idle
        }
    }
    
    fun reset() {
        _isLoading.value = false
        _error.value = null
        _data.value = null
        _uiState.value = UIState.Idle
    }
}

/**
 * UI状态定义
 */
sealed class UIState<out T> {
    object Idle : UIState<Nothing>()
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error<T>(val message: String, val data: T? = null) : UIState<T>()
    data class Empty(val message: String) : UIState<Nothing>()
}

/**
 * 执行异步操作并自动管理UI状态的扩展函数
 */
suspend fun <T> UIStateManager<T>.executeAsync(
    operation: suspend () -> T,
    errorConverter: (Exception) -> String = { it.message ?: "未知错误" }
) {
    try {
        setLoading()
        val result = operation()
        setData(result)
    } catch (e: Exception) {
        setError(errorConverter(e))
    }
}