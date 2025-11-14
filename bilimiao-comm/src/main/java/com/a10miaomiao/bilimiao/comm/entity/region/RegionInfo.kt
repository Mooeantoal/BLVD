package com.a10miaomiao.bilimiao.comm.entity.region

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * 分区信息
 */
@Parcelize
@Serializable
data class RegionInfo(
    var tid: Int,
    var reid: Int,
    var name: String,
    var logo: String,
    var type: Int = 0,
    var children: List<RegionChildrenInfo> = listOf()
) : Parcelable