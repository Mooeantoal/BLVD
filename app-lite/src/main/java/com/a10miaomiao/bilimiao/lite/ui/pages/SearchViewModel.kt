package com.a10miaomiao.bilimiao.lite.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.a10miaomiao.bilimiao.comm.apis.SearchAPI
import cn.a10miaomiao.bilimiao.comm.entity.archive.ArchiveInfo
import cn.a10miaomiao.bilimiao.comm.network.BiliApiService
import cn.a10miaomiao.bilimiao.comm.network.MiaoHttp
import cn.a10miaomiao.bilimiao.lite.network.NetworkExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class SearchViewModel : ViewModel() {
    
    private val searchAPI = SearchAPI()
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _searchResults = MutableStateFlow<List<ArchiveInfo>>(emptyList())
    val searchResults: StateFlow<List<ArchiveInfo>> = _searchResults
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory
    
    fun searchVideos(keyword: String, page: Int = 1) {
        if (keyword.isBlank()) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val response = searchAPI.archive(
                    keyword = keyword,
                    page = page,
                    pageSize = 20
                )
                
                val result = response.awaitCall()
                val responseBody = result.body?.string()
                
                if (responseBody.isNullOrEmpty()) {
                    throw MiaoHttp.ApiException("Empty response body")
                }
                
                // 解析搜索API响应
                val jsonResponse = json.decodeFromString<JsonObject>(responseBody)
                val data = jsonResponse["data"]?.jsonObject
                val resultData = data?.["result"]?.jsonArray ?: emptyList()
                
                val archiveList = resultData.mapNotNull { item ->
                    try {
                        parseArchiveInfo(item.jsonObject)
                    } catch (e: Exception) {
                        null
                    }
                }
                
                _searchResults.value = if (page == 1) {
                    archiveList
                } else {
                    _searchResults.value + archiveList
                }
                
                // 添加到搜索历史
                if (page == 1 && keyword.isNotBlank()) {
                    val newHistory = (_searchHistory.value + keyword).distinct().take(10)
                    _searchHistory.value = newHistory
                }
                
            } catch (e: Exception) {
                val networkError = NetworkExceptionHandler.handleException(e)
                _errorMessage.value = networkError.userFriendlyMessage
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun parseArchiveInfo(item: JsonObject): ArchiveInfo {
        return ArchiveInfo(
            author = item["author"]?.jsonPrimitive?.content ?: "未知UP主",
            bvid = item["bvid"]?.jsonPrimitive?.content ?: "",
            cover = item["pic"]?.jsonPrimitive?.content ?: "",
            ctime = item["pubdate"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
            danmaku = item["video_review"]?.jsonPrimitive?.content?.toString() ?: "0",
            duration = parseDuration(item["duration"]?.jsonPrimitive?.content ?: ""),
            first_cid = item["cid"]?.jsonPrimitive?.content?.toString() ?: "",
            goto = "av",
            icon_type = 0,
            is_cooperation = false,
            is_live_playback = false,
            is_pgc = false,
            is_popular = false,
            is_steins = false,
            is_ugcpay = false,
            length = item["duration"]?.jsonPrimitive?.content ?: "",
            `param` = item["aid"]?.jsonPrimitive?.content ?: "",
            play = formatPlayCount(item["play"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0),
            state = false,
            subtitle = item["description"]?.jsonPrimitive?.content?.take(100) ?: "",
            title = item["title"]?.jsonPrimitive?.content ?: "",
            tname = item["typename"]?.jsonPrimitive?.content ?: "",
            ugc_pay = 0,
            uri = "",
            videos = item["videos"]?.jsonPrimitive?.content?.toIntOrNull() ?: 1,
            view_content = formatPlayCount(item["play"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0)
        )
    }
    
    private fun parseDuration(durationStr: String): Long {
        return try {
            val parts = durationStr.split(":")
            when (parts.size) {
                2 -> parts[0].toLong() * 60 + parts[1].toLong()
                3 -> parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()
                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun formatPlayCount(count: Int): String {
        return when {
            count >= 100000000 -> "${count / 100000000}.${(count % 100000000) / 10000000}亿"
            count >= 10000 -> "${count / 10000}.${(count % 10000) / 1000}万"
            else -> count.toString()
        }
    }
    
    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}