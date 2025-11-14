package com.a10miaomiao.bilimiao.comm.entity.comm.bili

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class BadgeInfo(
    val bg_color: String,
    val color: String,
    val text: String
) : Parcelable