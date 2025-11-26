package com.a10miaomiao.bilimiao.lite.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.a10miaomiao.bilimiao.lite.network.NetworkError
import cn.a10miaomiao.bilimiao.lite.network.NetworkExceptionHandler

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    message: String = "加载中..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorContent(
    error: Throwable? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    customMessage: String? = null
) {
    val networkError = remember(error) {
        error?.let { NetworkExceptionHandler.handleException(it) }
    }
    
    val errorMessage = customMessage ?: networkError?.userFriendlyMessage 
        ?: error?.message 
        ?: "发生未知错误"
    
    val isRetryable = networkError?.isRetryable ?: true
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "错误",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = errorMessage,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            
            if (isRetryable && onRetry != null) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("重试")
                }
            }
        }
    }
}

@Composable
fun EmptyContent(
    message: String = "暂无内容",
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Search,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            action?.let { action ->
                Spacer(modifier = Modifier.height(24.dp))
                action()
            }
        }
    }
}

@Composable
fun LoadingAndErrorContent(
    isLoading: Boolean,
    error: Throwable? = null,
    isEmpty: Boolean = false,
    onRetry: (() -> Unit)? = null,
    emptyMessage: String = "暂无内容",
    loadingMessage: String = "加载中...",
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                LoadingContent(message = loadingMessage)
            }
            error != null -> {
                ErrorContent(error = error, onRetry = onRetry)
            }
            isEmpty -> {
                EmptyContent(message = emptyMessage)
            }
            else -> {
                content()
            }
        }
    }
}

@Composable
fun PullToRefreshLoading(
    refreshing: Boolean,
    modifier: Modifier = Modifier
) {
    if (refreshing) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp,
                strokeCap = StrokeCap.Round
            )
        }
    }
}