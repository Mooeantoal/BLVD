package com.a10miaomiao.bilimiao.lite.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.a10miaomiao.bilimiao.comm.apis.VideoAPI
import cn.a10miaomiao.bilimiao.comm.entity.archive.ArchiveInfo
import cn.a10miaomiao.bilimiao.comm.entity.video.VideoStatInfo
import cn.a10miaomiao.bilimiao.comm.network.MiaoHttp
import cn.a10miaomiao.bilimiao.download.DownloadService
import cn.a10miaomiao.bilimiao.lite.network.NetworkExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoDetailViewModel(
    private val videoAPI: VideoAPI
) : ViewModel() {

    data class VideoDetailData(
        val archiveInfo: ArchiveInfo,
        val statInfo: VideoStatInfo
    )

    private val _videoDetail = MutableStateFlow<VideoDetailData?>(null)
    val videoDetail: StateFlow<VideoDetailData?> = _videoDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadVideoDetail(videoId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // 根据ID类型调用不同API
                val response = when {
                    videoId.startsWith("BV") -> {
                        videoAPI.info(videoId.substring(2), "BV")
                    }
                    videoId.startsWith("av") -> {
                        videoAPI.info(videoId.substring(2), "AV")
                    }
                    else -> {
                        videoAPI.info(videoId, "AV")
                    }
                }

                val result = response.awaitCall()
                // TODO: 解析API响应，这里需要根据实际API结构来解析
                // 暂时使用模拟数据
                val mockArchiveInfo = ArchiveInfo(
                    author = "测试UP主",
                    bvid = videoId,
                    cover = "https://i0.hdslb.com/bfs/archive/5e5ff57caf46064c119f673cf6706fda05c5e4a6.jpg",
                    ctime = System.currentTimeMillis() / 1000,
                    danmaku = "259",
                    duration = 699,
                    first_cid = "1138560663",
                    goto = "av",
                    icon_type = 0,
                    is_cooperation = false,
                    is_live_playback = false,
                    is_pgc = false,
                    is_popular = false,
                    is_steins = false,
                    is_ugcpay = false,
                    length = "11:39",
                    `param` = "868892769",
                    play = "2.3万",
                    state = false,
                    subtitle = "",
                    title = "测试视频标题",
                    tname = "科技",
                    ugc_pay = 0,
                    uri = "",
                    videos = 1,
                    view_content = "2.3万"
                )

                val mockStatInfo = VideoStatInfo(
                    aid = videoId,
                    coin = 15,
                    danmaku = "259",
                    dislike = 0,
                    favorite = 42,
                    his_rank = 0,
                    like = 123,
                    now_rank = 0,
                    reply = 28,
                    share = 5,
                    view = "2.3万"
                )

                _videoDetail.value = VideoDetailData(mockArchiveInfo, mockStatInfo)

            } catch (e: Exception) {
                val networkError = NetworkExceptionHandler.handleException(e)
                _errorMessage.value = networkError.userFriendlyMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun shareVideo() {
        viewModelScope.launch {
            val videoData = _videoDetail.value ?: return@launch
            val shareText = "${videoData.archiveInfo.title}\nhttps://www.bilibili.com/video/${videoData.archiveInfo.bvid}"
            
            // TODO: 实现分享功能
            // 这需要Context来调用系统分享
        }
    }

    fun startDownload(videoDetail: VideoDetailData) {
        viewModelScope.launch {
            try {
                // TODO: 调用下载服务
                // 这里需要将videoDetail转换为下载服务需要的格式
                // DownloadService.startDownload(videoData.archiveInfo)
            } catch (e: Exception) {
                _errorMessage.value = "启动下载失败: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}