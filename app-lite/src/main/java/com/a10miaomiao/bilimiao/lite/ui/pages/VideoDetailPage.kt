package com.a10miaomiao.bilimiao.lite.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.a10miaomiao.bilimiao.comm.entity.archive.ArchiveInfo
import cn.a10miaomiao.bilimiao.comm.entity.video.VideoStatInfo
import cn.a10miaomiao.bilimiao.lite.ui.components.VideoPlayer
import cn.a10miaomiao.bilimiao.lite.ui.pages.VideoDetailViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailPage(
    videoId: String,
    onBackClick: () -> Unit,
    viewModel: VideoDetailViewModel = viewModel {
        VideoDetailViewModel(localDI.current.instance())
    }
) {
    val videoDetail by viewModel.videoDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(videoId) {
        viewModel.loadVideoDetail(videoId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("视频详情") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.shareVideo() }) {
                    Icon(Icons.Default.Share, contentDescription = "分享")
                }
            }
        )

        // 内容区域
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadVideoDetail(videoId) }) {
                            Text("重试")
                        }
                    }
                }
            }
            
            videoDetail != null -> {
                VideoDetailContent(
                    videoDetail = videoDetail!!,
                    onDownloadClick = { viewModel.startDownload(videoDetail!!) },
                    onPlayClick = { /* TODO: 播放视频 */ }
                )
            }
        }
    }
}

@Composable
private fun VideoDetailContent(
    videoDetail: VideoDetailViewModel.VideoDetailData,
    onDownloadClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 视频播放器区域
        VideoPlayer(
            coverUrl = videoDetail.archiveInfo.cover,
            onPlayClick = onPlayClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 视频标题
        Text(
            text = videoDetail.archiveInfo.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 视频信息
        VideoInfoSection(videoDetail)

        Spacer(modifier = Modifier.height(16.dp))

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPlayClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("播放")
            }

            Button(
                onClick = onDownloadClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("下载")
            }
        }
    }
}

@Composable
private fun VideoInfoSection(videoDetail: VideoDetailViewModel.VideoDetailData) {
    val archiveInfo = videoDetail.archiveInfo
    val statInfo = videoDetail.statInfo

    Column {
        // UP主信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "UP主: ${archiveInfo.author}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = archiveInfo.tname,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 视频统计数据
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "播放: ${statInfo.view}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "弹幕: ${statInfo.danmaku}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "时长: ${formatDuration(archiveInfo.duration)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 发布时间
        Text(
            text = "发布时间: ${formatTimestamp(archiveInfo.ctime)}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "${minutes}:${remainingSeconds.toString().padStart(2, '0')}"
}

private fun formatTimestamp(timestamp: Long): String {
    val date = java.util.Date(timestamp * 1000)
    val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}