package com.a10miaomiao.bilimiao.lite.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.a10miaomiao.bilimiao.download.DownloadService
import cn.a10miaomiao.bilimiao.lite.network.NetworkExceptionHandler
import kotlinx.coroutines.launch

class DownloadListViewModel : ViewModel() {
    
    fun getDownloadList(
        onSuccess: (List<cn.a10miaomiao.bilimiao.download.entry.BiliDownloadEntryAndPathInfo>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val service = DownloadService.instance
                if (service == null) {
                    onError("下载服务未启动")
                    return@launch
                }
                val list = service.downloadList
                onSuccess(list)
            } catch (e: Exception) {
                val networkError = NetworkExceptionHandler.handleException(e)
                onError(networkError.userFriendlyMessage)
            }
        }
    }
    
    fun deleteDownload(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val service = DownloadService.instance
                if (service == null) {
                    onError("下载服务未启动")
                    return@launch
                }
                
                // TODO: 实现删除下载逻辑
                // 这里需要调用服务的删除方法
                onSuccess()
            } catch (e: Exception) {
                val networkError = NetworkExceptionHandler.handleException(e)
                onError(networkError.userFriendlyMessage)
            }
        }
    }
}