package com.a10miaomiao.bilimiao.comm.utils

/**
 * 异常处理配置类
 * 用于配置应用级别的异常处理策略
 */
object ExceptionConfig {
    
    /**
     * 异常处理策略配置
     */
    data class ExceptionStrategy(
        /**
         * 是否启用详细的异常日志记录
         */
        val enableDetailedLogging: Boolean = true,
        
        /**
         * 是否启用异常上报到分析平台
         */
        val enableErrorReporting: Boolean = false,
        
        /**
         * 网络异常的重试策略
         */
        val networkRetryPolicy: NetworkRetryPolicy = NetworkRetryPolicy(),
        
        /**
         * 文件操作异常的处理策略
         */
        val fileOperationPolicy: FileOperationPolicy = FileOperationPolicy(),
        
        /**
         * 业务异常的处理策略
         */
        val businessErrorPolicy: BusinessErrorPolicy = BusinessErrorPolicy()
    )
    
    /**
     * 网络重试策略
     */
    data class NetworkRetryPolicy(
        /**
         * 最大重试次数
         */
        val maxRetries: Int = 3,
        
        /**
         * 重试延迟时间（毫秒）
         */
        val retryDelay: Long = 1000L,
        
        /**
         * 是否启用指数退避
         */
        val enableExponentialBackoff: Boolean = true,
        
        /**
         * 需要重试的状态码
         */
        val retryableStatusCodes: Set<Int> = setOf(500, 502, 503, 504)
    )
    
    /**
     * 文件操作策略
     */
    data class FileOperationPolicy(
        /**
         * 文件操作最大重试次数
         */
        val maxRetries: Int = 2,
        
        /**
         * 文件读写超时时间（毫秒）
         */
        val timeoutMs: Long = 30000L,
        
        /**
         * 是否启用文件校验
         */
        val enableFileValidation: Boolean = true
    )
    
    /**
     * 业务异常策略
     */
    data class BusinessErrorPolicy(
        /**
         * 是否显示用户友好的错误信息
         */
        val showUserFriendlyMessages: Boolean = true,
        
        /**
         * 需要静默处理的错误码
         */
        val silentErrorCodes: Set<Int> = setOf(-1, -2),
        
        /**
         * 需要重新登录的错误码
         */
        val reLoginErrorCodes: Set<Int> = setOf(-101, -401, -403)
    )
    
    // 默认配置
    val defaultConfig = ExceptionStrategy()
    
    // 生产环境配置
    val productionConfig = ExceptionStrategy(
        enableDetailedLogging = false,
        enableErrorReporting = true,
        networkRetryPolicy = NetworkRetryPolicy(maxRetries = 2),
        fileOperationPolicy = FileOperationPolicy(
            maxRetries = 1,
            timeoutMs = 15000L
        )
    )
    
    // 调试环境配置
    val debugConfig = ExceptionStrategy(
        enableDetailedLogging = true,
        enableErrorReporting = false,
        networkRetryPolicy = NetworkRetryPolicy(maxRetries = 5)
    )
    
    // 当前配置
    var currentConfig: ExceptionStrategy = defaultConfig
    
    /**
     * 获取当前配置
     */
    fun getConfig(): ExceptionStrategy = currentConfig
    
    /**
     * 设置配置
     */
    fun setConfig(config: ExceptionStrategy) {
        currentConfig = config
    }
    
    /**
     * 根据环境设置配置
     */
    fun setConfigForEnvironment(isDebug: Boolean, isProduction: Boolean) {
        currentConfig = when {
            isProduction -> productionConfig
            isDebug -> debugConfig
            else -> defaultConfig
        }
    }
}