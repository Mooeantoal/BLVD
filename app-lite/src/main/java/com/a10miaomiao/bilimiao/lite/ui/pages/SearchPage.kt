package com.a10miaomiao.bilimiao.lite.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.a10miaomiao.bilimiao.lite.ui.components.VideoItemCard
import cn.a10miaomiao.bilimiao.lite.ui.components.LoadingAndErrorContent
import cn.a10miaomiao.bilimiao.lite.ui.components.ErrorMessage
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.onKeyEvent
import androidx.compose.ui.keykey.Key
import androidx.compose.ui.keykey.KeyEvent
import cn.a10miaomiao.bilimiao.lite.ui.pages.SearchViewModel

@Composable
fun SearchPage(
    onVideoClick: (String) -> Unit,
    viewModel: SearchViewModel = remember { SearchViewModel() }
) {
    var searchText by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 搜索框
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("搜索视频") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                onKeyEvent = { event ->
                    if (event.key == Key.Enter && searchText.isNotBlank()) {
                        viewModel.searchVideos(searchText)
                        true
                    } else {
                        false
                    }
                }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    if (searchText.isNotBlank()) {
                        viewModel.searchVideos(searchText)
                    }
                },
                enabled = searchText.isNotBlank() && !isLoading
            ) {
                Text("搜索")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 错误提示
        errorMessage?.let { message ->
            ErrorMessage(
                message = message,
                onRetry = { 
                    if (searchText.isNotBlank()) {
                        viewModel.searchVideos(searchText)
                    }
                },
                onDismiss = { viewModel.clearError() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 搜索结果
        LoadingAndErrorContent(
            isLoading = isLoading,
            error = errorMessage,
            isEmpty = searchResults.isEmpty() && searchText.isNotBlank(),
            emptyMessage = "未找到相关视频，试试其他关键词",
            onRetry = { 
                if (searchText.isNotBlank()) {
                    viewModel.searchVideos(searchText)
                }
            }
        ) {
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults) { video ->
                        VideoItemCard(
                            video = video,
                            onClick = { onVideoClick(video.bvid) }
                        )
                    }
                }
            } else if (searchHistory.isNotEmpty() && searchText.isBlank()) {
                // 搜索历史
                Column {
                    Text(
                        text = "搜索历史",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchHistory) { historyItem ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = historyItem,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { 
                                                searchText = historyItem
                                                viewModel.searchVideos(historyItem)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 空状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "输入关键词开始搜索",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}