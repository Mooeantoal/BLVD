package com.a10miaomiao.bilimiao.comm

import com.a10miaomiao.bilimiao.comm.utils.BiliGeetestUtil
import kotlinx.serialization.Serializable
import org.json.JSONObject

// Geetest SDK 已移除，提供空实现
class BiliGeetestUtilImpl : BiliGeetestUtil {
    
    override fun startCustomFlow(gtCallBack: BiliGeetestUtil.GTCallBack) {
        // Geetest 功能已被移除
    }

    @Serializable
    data class GT3ResultBean(
        val geetest_challenge: String,
        val geetest_seccode: String,
        val geetest_validate: String,
    )

    interface GTCallBack {
        suspend fun onGTDialogResult(
            result: GT3ResultBean,
        ): Boolean
        suspend fun getGTApiJson(): JSONObject?
    }
}