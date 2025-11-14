package com.a10miaomiao.bilimiao

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.a10miaomiao.bilimiao.download.DownloadService
import com.a10miaomiao.bilimiao.comm.delegate.theme.ThemeDelegate
import com.a10miaomiao.bilimiao.comm.scanner.BilimiaoScanner
import com.a10miaomiao.bilimiao.comm.utils.miaoLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton

class MainActivity
    : AppCompatActivity(),
    DIAware, CoroutineScope {

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    override val di: DI = DI.lazy {
        bindSingleton { this@MainActivity }
    }

    private val themeDelegate by lazy { ThemeDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeDelegate.onCreate(savedInstanceState)

        // 请求存储权限
        requestStoragePermission()

        // 启动下载服务
        DownloadService.startService(this)

        // 显示简单的界面提示
        setContentView(createDownloadView())
    }

    private fun createDownloadView(): android.widget.TextView {
        val textView = android.widget.TextView(this).apply {
            text = "视频下载器\n\n请使用其他B站客户端获取视频链接，然后在此应用中下载"
            gravity = android.view.Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18f)
        }
        return textView
    }

    private fun requestStoragePermission() {
        // 安卓6.0及以上需要动态申请存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    100
                )
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BilimiaoScanner.REQUEST_CODE -> {
                BilimiaoScanner.onActivityResult(
                    ActivityResult(resultCode, data)
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            // 权限申请结果处理
            miaoLogger() debug "Storage permission result: ${grantResults.joinToString()}"
        }
    }
}