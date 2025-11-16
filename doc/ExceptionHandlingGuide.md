# 异常处理改进指南

## 概述

本文档介绍了BLVD项目中改进的异常处理机制，包括统一异常分类、安全操作方法和异常处理配置。

## 异常分类

### 应用特定异常 (AppException)

项目定义了以下应用特定异常类型：

```kotlin
// 网络异常
ExceptionHandler.AppException.NetworkException(url, statusCode, cause)

// 数据解析异常  
ExceptionHandler.AppException.ParseException(dataType, rawData, cause)

// 业务逻辑异常
ExceptionHandler.AppException.BusinessException(code, message)

// 文件操作异常
ExceptionHandler.AppException.FileException(operation, filePath, cause)
```

## 使用方法

### 1. 安全执行操作

使用 `safeExecute` 提供默认值：

```kotlin
val result = ExceptionHandler.safeExecute("operation name", defaultValue) {
    // 可能抛出异常的代码
    performOperation()
}
```

### 2. 使用 Result 包装

使用 `safeResult` 返回 `Result<T>`：

```kotlin
val result = ExceptionHandler.safeResult("parse JSON") {
    parseJson(data)
}

result.onSuccess { data ->
    // 处理成功
}.onFailure { error ->
    // 处理失败
}
```

### 3. 网络请求安全调用

使用 `safeNetworkCall` 处理网络请求：

```kotlin
val result = ExceptionHandler.safeNetworkCall(url, "download file") {
    downloadFile(url)
}
```

### 4. 自定义异常处理

```kotlin
ExceptionHandler.handleException(exception, "operation context")
```

### 5. 协程异常处理

```kotlin
val exceptionHandler = ExceptionHandler.createCoroutineExceptionHandler("coroutine context")

launch(exceptionHandler) {
    // 协程代码
}
```

## 配置异常处理策略

### 设置配置

```kotlin
// 生产环境配置
ExceptionConfig.setConfig(ExceptionConfig.productionConfig)

// 调试环境配置  
ExceptionConfig.setConfig(ExceptionConfig.debugConfig)

// 自定义配置
val customConfig = ExceptionConfig.ExceptionStrategy(
    enableDetailedLogging = true,
    networkRetryPolicy = ExceptionConfig.NetworkRetryPolicy(maxRetries = 5)
)
ExceptionConfig.setConfig(customConfig)
```

## 在现有代码中的集成示例

### 改进前

```kotlin
try {
    val response = httpClient.execute(request)
    // 处理响应
} catch (e: IOException) {
    e.printStackTrace()
    // 错误处理
}
```

### 改进后

```kotlin
ExceptionHandler.safeNetworkCall(url, "HTTP request") {
    val response = httpClient.execute(request)
    response
}.onSuccess { response ->
    // 处理成功响应
}.onFailure { error ->
    // 错误处理，包括异常分类和日志记录
}
```

## 异常处理配置项

### 网络重试策略

- 最大重试次数: 默认3次，生产环境2次，调试环境5次
- 重试延迟: 默认1000ms
- 指数退避: 启用
- 可重试状态码: 500, 502, 503, 504

### 文件操作策略

- 最大重试次数: 默认2次
- 超时时间: 默认30000ms
- 文件校验: 启用

### 业务异常策略

- 用户友好消息: 启用
- 静默错误码: [-1, -2]
- 需重新登录错误码: [-101, -401, -403]

## 最佳实践

1. **统一异常处理**: 所有可能抛出异常的操作都应使用安全方法包装
2. **具体异常信息**: 提供明确的操作名称和上下文信息
3. **适当的重试策略**: 根据操作类型设置合适的重试次数
4. **用户友好错误**: 对业务异常提供用户友好的错误信息
5. **异常分类**: 使用适当的异常分类帮助调试和监控

## 监控和调试

### 启用详细日志

```kotlin
ExceptionConfig.setConfig(ExceptionConfig.debugConfig)
```

### 错误上报

生产环境会自动启用错误上报功能，可以集成到现有的监控系统中。

## 迁移指南

1. 识别项目中现有的 `try-catch` 块
2. 将简单的 `printStackTrace()` 调用替换为 `ExceptionHandler.handleException()`
3. 将可能失败的操作包装在安全方法中
4. 设置适当的异常处理配置
5. 测试异常处理流程确保正确性

## 注意事项

- 避免在安全方法中捕获并忽略异常
- 为不同环境设置合适的配置
- 确保异常信息对用户友好但又不泄露敏感信息
- 定期检查异常处理策略的有效性