package com.a10miaomiao.bilimiao.comm.utils

import java.text.SimpleDateFormat
import java.util.*

object NumberUtil {
    fun converString(num: Long): String {
        if (num < 10000) {
            return num.toString()
        }
        var unit = "万"
        var newNum = num / 10000.0
        if (num > 9999_9999){
            unit = "亿"
            newNum = num / 10000_0000.0
        }
        val numStr = String.format("%." + 1 + "f", newNum)
        return numStr + unit
    }

    fun converString(num: Int): String {
        if (num < 10000) {
            return num.toString()
        }
        var unit = "万"
        var newNum = num / 10000.0
        if (num > 9999_9999){
            unit = "亿"
            newNum = num / 10000_0000.0
        }
        val numStr = String.format("%." + 1 + "f", newNum)
        return numStr + unit
    }

    fun converString(num: String): String {
        return try {
            if (TypeSafe.isInteger(num)) {
                converString(TypeSafe.safeToInt(num))
            } else {
                num
            }
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "converString: $num")
            num
        }
    }

    fun converStringOrNull(num: Long?): String? {
        if (num == null) return null
        return converString(num)
    }

    fun converStringOrNull(num: Int?): String? {
        if (num == null) return null
        return converString(num)
    }

    fun converStringOrNull(num: String?): String? {
        if (num == null) return null
        return converString(num)
    }


    fun converDuration(duration: Long): String {
        var s = (duration % 60).toString()
        var min = (duration / 60).toString()
        if (s.length == 1)
            s = "0$s"
        if (min.length == 1)
            min = "0$min"
        return "$min:$s"
    }

    fun converDuration(duration: String): String {
        return try {
            if (TypeSafe.isInteger(duration)) {
                converDuration(TypeSafe.safeToInt(duration))
            } else {
                duration
            }
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "converDuration: $duration")
            duration
        }
    }

    fun converDuration(duration: Int): String {
        var s = (duration % 60).toString()
        var min = (duration / 60).toString()
        if (s.length == 1)
            s = "0$s"
        if (min.length == 1)
            min = "0$min"
        return "$min:$s"
    }

    fun converCTime(ctime: Long?): String {
        if (ctime == null) {
            return ""
        }
        val date = Date(ctime * 1000)
        val now = Calendar.getInstance().timeInMillis
        val deltime = (now - date.time) / 1000
        return when {
            deltime > 30 * 24 * 60 * 60 -> {
                val sf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                sf.format(date)
            }
            deltime > 24 * 60 * 60 -> TypeSafe.safeToString(deltime / (24 * 60 * 60).toInt()) + "天前"
            deltime > 60 * 60 -> TypeSafe.safeToString(deltime / (60 * 60).toInt()) + "小时前"
            deltime > 60 -> TypeSafe.safeToString(deltime / 60.toInt()) + "分钟前"
            else -> TypeSafe.safeToString(deltime) + "秒前"
        }
    }

    fun isNumber(text: String): Boolean {
        return TypeSafe.isInteger(text)
    }
}