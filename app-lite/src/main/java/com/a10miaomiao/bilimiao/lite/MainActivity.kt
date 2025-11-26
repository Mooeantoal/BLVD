package com.a10miaomiao.bilimiao.lite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import cn.a10miaomiao.bilimiao.compose.ComposeFragment
import cn.a10miaomiao.bilimiao.compose.StartViewWrapper
import com.a10miaomiao.bilimiao.comm.datastore.SettingConstants
import com.a10miaomiao.bilimiao.comm.delegate.helper.StatusBarHelper
import com.a10miaomiao.bilimiao.comm.delegate.player.BasePlayerDelegate
import com.a10miaomiao.bilimiao.comm.delegate.theme.ThemeDelegate
import com.a10miaomiao.bilimiao.comm.utils.miaoLogger
import com.a10miaomiao.bilimiao.config.config
import com.a10miaomiao.bilimiao.lite.ui.MainFragment
import com.a10miaomiao.bilimiao.lite.ui.MainUi
import com.a10miaomiao.bilimiao.lite.ui.startViewWrapper
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.widget.scaffold.ScaffoldView
import com.a10miaomiao.bilimiao.widget.scaffold.getScaffoldView
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.DynamicScheme
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import splitties.views.backgroundColor

class MainActivity : AppCompatActivity(), DIAware {

    private var mainUi: MainUi? = null
    private val ui get() = mainUi!!

    override val di: DI = DI.lazy {
        bindSingleton { this@MainActivity }
        bindSingleton { basePlayerDelegate }
        bindSingleton { themeDelegate }
        bindSingleton { statusBarHelper }
        bindSingleton { startViewWrapper }
    }

    private val basePlayerDelegate by lazy { BasePlayerDelegate(this) }
    private val themeDelegate by lazy { ThemeDelegate(this) }
    private val statusBarHelper by lazy { StatusBarHelper(this) }
    
    private var fragment: Fragment? = null
    private val scaffoldView: ScaffoldView get() = getScaffoldView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        mainUi = MainUi(this, startViewWrapper)
        setContentView(ui.root)

        startDownloadService()
        initStatusBar()
        initFragment()
        initTheme()
    }

    override fun onResume() {
        super.onResume()
        statusBarHelper.setStatusBarColorTransparent()
    }

    override fun onDestroy() {
        super.onDestroy()
        basePlayerDelegate.release()
        fragment = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && 
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragment?.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                    )
                }
            }
        }
    }

    private fun startDownloadService() {
        DownloadService.startService(this)
    }

    private fun initStatusBar() {
        statusBarHelper.setStatusBarColorTransparent()
        statusBarHelper.setLightStatusBar(config.isDarkTheme)
    }

    private fun initFragment() {
        val fragmentManager = supportFragmentManager
        fragment = fragmentManager.findFragmentByTag(MainFragment.TAG)
        if (fragment == null) {
            fragment = ComposeFragment(MainFragment.TAG) {
                MainFragment()
            }
            fragmentManager.beginTransaction()
                .replace(ui.mContainerView.id, fragment!!, MainFragment.TAG)
                .commit()
        }
    }

    private fun initTheme() {
        lifecycleScope.launch {
            config.themeState
                .mapNotNull { it }
                .flowOn(kotlinx.coroutines.Dispatchers.IO)
                .launchIn(this)
        }
    }

    fun requestPermissions(permissions: Array<String>): ActivityResult {
        ActivityCompat.requestPermissions(this, permissions, 1000)
        return ActivityResult(1, null)
    }
}