package com.a10miaomiao.bilimiao.lite.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.a10miaomiao.bilimiao.lite.ui.components.VideoDownloadCard
import cn.a10miaomiao.bilimiao.lite.ui.components.LoadingAndErrorContent
import cn.a10miaomiao.bilimiao.lite.ui.pages.DownloadListViewModel
import cn.a10miaomiao.bilimiao.download.entry.BiliDownloadEntryAndPathInfo

@Composable
fun DownloadListPage(
    onVideoPlay: (videoPath: String, title: String) -> Unit,
    viewModel: DownloadListViewModel = remember { DownloadListViewModel() }
) {
    var downloadList by remember { mutableStateOf<List<BiliDownloadEntryAndPathInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 加载下载列表
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        viewModel.getDownloadList(
            onSuccess = { list ->
                downloadList = list
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    LoadingAndErrorContent(
        isLoading = isLoading,
        error = errorMessage?.let { RuntimeException(it) },
        isEmpty = downloadList.isEmpty(),
        emptyMessage = "暂无下载任务，去搜索页下载视频吧",
        onRetry = {
            isLoading = true
            errorMessage = null
            viewModel.getDownloadList(
                onSuccess = { list ->
                    downloadList = list
                    isLoading = false
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(downloadList) { downloadInfo ->
                VideoDownloadCard(
                    downloadInfo = downloadInfo,
                    onPlayClick = { 
                        val videoPath = "${downloadInfo.entryDirPath}/${downloadInfo.entry.type_tag}/video.m4s"
                        onVideoPlay(videoPath, downloadInfo.entry.name)
                    },
                    onDeleteClick = { 
                        viewModel.deleteDownload(
                            downloadInfo.entry.key,
                            onSuccess = {
                                // 重新加载列表
                                viewModel.getDownloadList(
                                    onSuccess = { list ->
                                        downloadList = list
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    }
                )
            }
        }
    }
}