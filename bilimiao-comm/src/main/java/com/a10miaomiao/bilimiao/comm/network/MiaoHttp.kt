package com.a10miaomiao.bilimiao.comm.network

import android.webkit.CookieManager
import com.a10miaomiao.bilimiao.comm.BilimiaoCommApp
import com.a10miaomiao.bilimiao.comm.miao.MiaoJson
import com.a10miaomiao.bilimiao.comm.utils.ExceptionHandler
import com.a10miaomiao.bilimiao.comm.utils.miaoLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MiaoHttp(var url: String? = null) {
    private val cookieManager by lazy {
        ExceptionHandler.safeExecute("cookie manager initialization", null) {
            CookieManager.getInstance()
        }
    }

    private var client = OkHttpClient()
    private val requestBuilder = Request.Builder()
    val headers = mutableMapOf<String, String>()
    var method = GET

    var body: RequestBody? = null
    var formBody: Map<String, String?>? = null

    private fun buildRequest(): Request {
        requestBuilder.addHeader("User-Agent", ApiHelper.USER_AGENT)
        requestBuilder.addHeader("Referer", ApiHelper.REFERER)
        requestBuilder.addHeader("buvid", BilimiaoCommApp.commApp.getBilibiliBuvid())
        if (url?.let { "bilibili.com" in it } == true) {
            requestBuilder.addHeader("env", "prod")
            requestBuilder.addHeader("app-key", "android_hd")
            BilimiaoCommApp.commApp.loginInfo?.token_info?.let{
                requestBuilder.addHeader("x-bili-mid", it.mid.toString())
            }
        }
        val cookie = getCookie(url)
        if (!cookie.isNullOrBlank()) {
            requestBuilder.addHeader("Cookie", cookie)
        }
        for (key in headers.keys) {
            val headerValue = headers[key] ?: throw ExceptionHandler.AppException.BusinessException(
                code = -1,
                message = "Header value for '$key' is null"
            )
            requestBuilder.addHeader(key, headerValue)
        }

        if (body == null && formBody != null) {
            val bodyStr = ApiHelper.urlencode(formBody ?: emptyMap())
            body = bodyStr.toRequestBody(
                "application/x-www-form-urlencoded".toMediaType()
            )
        }
        val reqUrl = url ?: throw ExceptionHandler.AppException.BusinessException(
            code = -1,
            message = "URL cannot be null for HTTP request"
        )
        val req = requestBuilder.method(method, body)
            .url(reqUrl)
            .build()
        return req
    }

    fun call(): Response {
        val req = buildRequest()
        return ExceptionHandler.safeResult("HTTP call to ${url}") {
            client.newCall(req).execute()
        }.getOrThrow()
    }

    private fun getCookie(url: String?): String {
        return ExceptionHandler.safeExecute("get cookie for $url", "") {
            cookieManager?.getCookie(url) ?: ""
        }
    }

    suspend fun awaitCall(): Response{
        miaoLogger().d(
            "method" to method,
            "url" to url,
            "formBody" to formBody
        )
        
        return ExceptionHandler.safeNetworkCall(url ?: "unknown", "HTTP await call") {
            suspendCancellableCoroutine { continuation ->
                val req = buildRequest()
                val call = client.newCall(req)
                continuation.invokeOnCancellation {
                    call.cancel()
                }
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resumeWithException(e)
                    }
                    override fun onResponse(call: Call, response: Response) {
                        continuation.resume(response)
                    }
                })
            }
        }.getOrThrow()
    }

    fun get(): Response {
        method = GET
        return call()
    }

    fun post(): Response {
        method = POST
        return call()
    }

//    fun <T> responseType<>() {
//
//    }

    companion object {

        fun request(url: String? = null, init: (MiaoHttp.() -> Unit)? = null) = MiaoHttp(url).apply {
            init?.invoke(this)
        }

        fun Response.string(): String {
            return this.body?.string() ?: throw ExceptionHandler.AppException.ParseException(
                dataType = "HTTP response body",
                message = "Response body is null"
            )
        }

        inline fun <reified T> Response.json(isLog: Boolean = false): T {
            val jsonStr = ExceptionHandler.safeResult("parse HTTP response body") {
                this.string()
            }.getOrThrow()
            
            if (isLog) {
                miaoLogger() debug jsonStr
            }
            
            return ExceptionHandler.safeResult("parse JSON to ${T::class.simpleName}") {
                MiaoJson.fromJson<T>(jsonStr)
            }.getOrThrow()
        }

        const val GET = "GET"
        const val POST = "POST"

    }
}
