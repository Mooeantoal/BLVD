package com.a10miaomiao.bilimiao.lite.ui

import android.content.Context
import android.view.View
import cn.a10miaomiao.bilimiao.compose.base.ComposePage
import cn.a10miaomiao.bilimiao.compose.base.PageSearchMethod

class StartViewWrapper(
    private val context: Context,
    private val onNavigate: (ComposePage) -> Unit = {},
    private val onNavigateUrl: (String) -> Unit = {},
    private val onDismissRequest: () -> Unit = {},
    private val onOpenScanner: ((result: String) -> Unit) -> Boolean = { false }
) {
    
    private var view: View? = null
    
    fun getView(): View {
        return view ?: createDefaultView()
    }
    
    private fun createDefaultView(): View {
        return View(context).apply {
            view = this
        }
    }
    
    fun setPageSearchMethod(searchMethod: PageSearchMethod?) {
        // 精简版暂不支持页面内搜索
    }
    
    fun openSearchDialog(keyword: String, mode: Int, showHistory: Boolean) {
        // 精简版暂不支持搜索对话框
    }
    
    fun closeSearchDialog() {
        // 精简版暂不支持搜索对话框
    }
    
    fun setTouchStartTop(top: Float) {
        // 精简版暂不支持触摸处理
    }
    
    var showSearchDialog: Boolean = false
        private set
}