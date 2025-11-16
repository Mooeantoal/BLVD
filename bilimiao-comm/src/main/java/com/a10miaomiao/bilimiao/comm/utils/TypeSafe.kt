package com.a10miaomiao.bilimiao.comm.utils

import com.a10miaomiao.bilimiao.comm.utils.ExceptionHandler.AppException

/**
 * 类型安全转换工具类
 * 提供安全的类型转换方法，避免类型转换异常
 */
object TypeSafe {
    
    /**
     * 安全地将字符串转换为整数
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时的默认值，默认为0
     * @return 转换后的整数值
     */
    fun safeToInt(str: String?, defaultValue: Int = 0): Int {
        return if (str == null) defaultValue else {
            try {
                str.toInt()
            } catch (e: NumberFormatException) {
                ExceptionHandler.handleException(e, "safeToInt: $str")
                defaultValue
            }
        }
    }
    
    /**
     * 安全地将字符串转换为长整数
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时的默认值，默认为0L
     * @return 转换后的长整数值
     */
    fun safeToLong(str: String?, defaultValue: Long = 0L): Long {
        return if (str == null) defaultValue else {
            try {
                str.toLong()
            } catch (e: NumberFormatException) {
                ExceptionHandler.handleException(e, "safeToLong: $str")
                defaultValue
            }
        }
    }
    
    /**
     * 安全地将字符串转换为浮点数
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时的默认值，默认为0.0f
     * @return 转换后的浮点数值
     */
    fun safeToFloat(str: String?, defaultValue: Float = 0.0f): Float {
        return if (str == null) defaultValue else {
            try {
                str.toFloat()
            } catch (e: NumberFormatException) {
                ExceptionHandler.handleException(e, "safeToFloat: $str")
                defaultValue
            }
        }
    }
    
    /**
     * 安全地将字符串转换为双精度浮点数
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时的默认值，默认为0.0
     * @return 转换后的双精度浮点数值
     */
    fun safeToDouble(str: String?, defaultValue: Double = 0.0): Double {
        return if (str == null) defaultValue else {
            try {
                str.toDouble()
            } catch (e: NumberFormatException) {
                ExceptionHandler.handleException(e, "safeToDouble: $str")
                defaultValue
            }
        }
    }
    
    /**
     * 安全地将字符串转换为布尔值
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时的默认值，默认为false
     * @return 转换后的布尔值
     */
    fun safeToBoolean(str: String?, defaultValue: Boolean = false): Boolean {
        return if (str == null) defaultValue else {
            when (str.lowercase()) {
                "true", "1", "yes", "y" -> true
                "false", "0", "no", "n" -> false
                else -> defaultValue
            }
        }
    }
    
    /**
     * 安全地将可为空的字符串转换为非空字符串
     * @param str 要转换的字符串
     * @param defaultValue 转换失败时的默认值，默认为空字符串
     * @return 非空字符串
     */
    fun safeToString(str: String?, defaultValue: String = ""): String {
        return str ?: defaultValue
    }
    
    /**
     * 安全地将任意类型转换为字符串
     * @param value 要转换的值
     * @param defaultValue 转换失败时的默认值，默认为空字符串
     * @return 转换后的字符串
     */
    fun <T> safeToString(value: T?, defaultValue: String = ""): String {
        return try {
            value?.toString() ?: defaultValue
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "safeToString: $value")
            defaultValue
        }
    }
    
    /**
     * 安全的类型转换（智能转换）
     * @param value 要转换的值
     * @param defaultValue 转换失败时的默认值
     * @return 转换后的值，如果无法转换则返回默认值
     */
    inline fun <reified T> safeCast(value: Any?, defaultValue: T): T {
        return try {
            value as? T ?: defaultValue
        } catch (e: Exception) {
            ExceptionHandler.handleException(e, "safeCast: ${value?.javaClass?.simpleName} to ${T::class.simpleName}")
            defaultValue
        }
    }
    
    /**
     * 安全的类型转换，带自定义错误处理
     * @param value 要转换的值
     * @param onError 错误处理函数
     * @return 转换后的值，如果无法转换则执行错误处理函数并返回null
     */
    inline fun <reified T> safeCast(value: Any?, onError: (Exception) -> Unit): T? {
        return try {
            value as? T
        } catch (e: Exception) {
            onError(e)
            ExceptionHandler.handleException(e, "safeCast with error handler: ${value?.javaClass?.simpleName} to ${T::class.simpleName}")
            null
        }
    }
    
    /**
     * 检查字符串是否为有效的数字
     * @param str 要检查的字符串
     * @return 如果是有效的数字返回true，否则返回false
     */
    fun isNumeric(str: String?): Boolean {
        return str?.matches(Regex("^[-+]?\\d+([.]\\d+)?$")) ?: false
    }
    
    /**
     * 检查字符串是否为有效的整数
     * @param str 要检查的字符串
     * @return 如果是有效的整数返回true，否则返回false
     */
    fun isInteger(str: String?): Boolean {
        return str?.matches(Regex("^[-+]?\\d+$")) ?: false
    }
    
    /**
     * 检查字符串是否为有效的浮点数
     * @param str 要检查的字符串
     * @return 如果是有效的浮点数返回true，否则返回false
     */
    fun isFloat(str: String?): Boolean {
        return str?.matches(Regex("^[-+]?\\d+([.]\\d+)?$")) ?: false
    }
}