package com.a10miaomiao.bilimiao.lite.utils

import android.content.Context
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger

/**
 * 图片加载配置 - 优化内存使用和性能
 */
object ImageLoadingConfig {

    fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 使用25%的可用内存作为图片缓存
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB磁盘缓存
                    .build()
            }
            .respectCacheHeaders(false) // 忽略服务器的缓存头，使用我们自己的缓存策略
            .memoryCachePolicy(CachePolicy.ENABLED) // 启用内存缓存
            .diskCachePolicy(CachePolicy.ENABLED) // 启用磁盘缓存
            .networkCachePolicy(CachePolicy.ENABLED) // 启用网络缓存
            .components {
                add(SvgDecoder.Factory())
                add(GifDecoder.Factory())
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}