package com.a10miaomiao.bilimiao.lite.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import kotlin.system.measureTimeMillis

/**
 * 性能监控工具
 */
object PerformanceMonitor {

    private const val TAG = "PerformanceMonitor"

    /**
     * 获取当前应用内存使用情况
     */
    fun getMemoryInfo(context: Context): MemoryInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory

        return MemoryInfo(
            totalMemory = totalMemory,
            usedMemory = usedMemory,
            freeMemory = freeMemory,
            maxMemory = runtime.maxMemory(),
            systemAvailableMemory = memoryInfo.availMem,
            systemTotalMemory = memoryInfo.totalMem,
            lowMemory = memoryInfo.lowMemory
        )
    }

    /**
     * 记录内存使用情况
     */
    fun logMemoryUsage(context: Context, tag: String = TAG) {
        val memoryInfo = getMemoryInfo(context)
        Log.d(tag, """
            内存使用情况:
            应用总内存: ${formatBytes(memoryInfo.totalMemory)}
            应用已用内存: ${formatBytes(memoryInfo.usedMemory)}
            应用可用内存: ${formatBytes(memoryInfo.freeMemory)}
            应用最大内存: ${formatBytes(memoryInfo.maxMemory)}
            系统可用内存: ${formatBytes(memoryInfo.systemAvailableMemory)}
            系统总内存: ${formatBytes(memoryInfo.systemTotalMemory)}
            系统低内存: ${memoryInfo.lowMemory}
            应用内存使用率: ${String.format("%.1f", (memoryInfo.usedMemory.toFloat() / memoryInfo.maxMemory * 100))}%
        """.trimIndent())
    }

    /**
     * 测量代码块执行时间
     */
    inline fun <T> measureTime(
        tag: String = TAG,
        message: String = "Execution time",
        block: () -> T
    ): T {
        val time = measureTimeMillis {
            return block()
        }
        Log.d(tag, "$message: ${time}ms")
        return block()
    }

    /**
     * 检查是否需要释放内存
     */
    fun shouldTrimMemory(context: Context): Boolean {
        val memoryInfo = getMemoryInfo(context)
        val usageRatio = memoryInfo.usedMemory.toFloat() / memoryInfo.maxMemory
        return usageRatio > 0.8f || memoryInfo.lowMemory
    }

    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> "%.1f GB".format(gb)
            mb >= 1 -> "%.1f MB".format(mb)
            kb >= 1 -> "%.1f KB".format(kb)
            else -> "$bytes B"
        }
    }

    data class MemoryInfo(
        val totalMemory: Long,
        val usedMemory: Long,
        val freeMemory: Long,
        val maxMemory: Long,
        val systemAvailableMemory: Long,
        val systemTotalMemory: Long,
        val lowMemory: Boolean
    )
}