package com.a10miaomiao.bilimiao.lite

import android.app.Application
import cn.a10miaomiao.bilimiao.comm.BilimiaoCommApp
import cn.a10miaomiao.bilimiao.comm.config.*

class BilimiaoLiteApp : Application(), BilimiaoCommApp {
    
    override val commApp: BilimiaoCommApp
        get() = this
    
    override val application: Application
        get() = this
        
    override fun onCreate() {
        super.onCreate()
        
        // 初始化配置
        config.initialize(this)
        
        // 初始化网络
        initNetwork()
        
        // 初始化依赖注入
        initDI()
    }
    
    private fun initNetwork() {
        // 网络配置初始化
    }
    
    private fun initDI() {
        // 依赖注入初始化
    }
}