# Bilibili Lite - 精简版应用

这是一个基于原版 bilimiao2 项目裁剪的精简版本，只保留核心的视频搜索、详情和下载功能。

## 🎯 功能特性

### ✅ 保留功能
- **视频搜索** - 搜索B站视频内容
- **视频详情** - 查看视频详细信息
- **视频下载** - 下载视频到本地存储
- **本地播放** - 播放已下载的视频

### ❌ 移除功能
- 用户动态/社交功能
- 直播相关功能
- 评论系统
- 用户个人中心
- 桌面小部件
- 复杂的主题系统
- 性能测试模块

## 📁 项目结构

```
bilimiao-lite/
├── app-lite/                    # 主应用模块
│   ├── src/main/java/.../lite/
│   │   ├── MainActivity.kt      # 主活动
│   │   ├── BilimiaoLiteApp.kt   # 应用类
│   │   └── ui/                 # UI层
│   │       ├── MainFragment.kt   # 主界面
│   │       ├── MainUi.kt        # 主UI布局
│   │       ├── pages/           # 页面
│   │       │   ├── SearchPage.kt         # 搜索页
│   │       │   ├── DownloadListPage.kt    # 下载列表页
│   │       │   ├── SearchViewModel.kt      # 搜索VM
│   │       │   └── DownloadListViewModel.kt # 下载列表VM
│   │       └── components/      # UI组件
│   │           ├── VideoItemCard.kt        # 视频卡片
│   │           └── VideoDownloadCard.kt   # 下载视频卡片
│   ├── build.gradle.kts         # 模块构建配置
│   └── src/main/AndroidManifest.xml
├── bilimiao-comm/              # 公共基础库 (原版)
├── bilimiao-download/          # 下载模块 (原版)
├── DanmakuFlameMaster/         # 弹幕引擎 (原版)
├── bilimiao-compose-lite/       # 精简UI库
└── settings-lite.gradle.kts     # 精简版设置
```

## 🚀 构建方法

### 1. 使用精简版配置
```bash
# 复制精简版配置
cp settings-lite.gradle.kts settings.gradle.kts
cp build-lite.gradle.kts build.gradle.kts

# 或者直接使用 gradle -c 参数
./gradlew -c settings-lite.gradle.kts assembleDebug
```

### 2. 构建APK
```bash
# Debug版本
./gradlew assembleDebug

# Release版本
./gradlew assembleRelease
```

## 🔧 核心模块说明

### bilimiao-comm
保留完整的公共基础库，包括：
- 网络请求封装
- API接口定义
- 数据模型
- 工具类

### bilimiao-download
保留完整的下载功能，包括：
- 视频下载管理
- 下载队列
- 本地播放支持

### bilimiao-compose-lite
精简版的UI组件库，只保留：
- 搜索相关组件
- 视频列表组件
- 下载管理组件
- 基础UI组件

## 📱 应用界面

### 主界面
- 底部导航：搜索、下载
- 顶部标题栏：应用名称和搜索图标

### 搜索页面
- 搜索输入框
- 搜索结果列表
- 视频卡片显示标题、UP主、播放量等

### 下载页面
- 下载任务列表
- 下载进度显示
- 播放和删除操作

## 🎨 UI特点
- Material Design 3 设计语言
- Jetpack Compose 现代化UI
- 响应式布局
- 深色/浅色主题支持

## 📦 依赖精简

### 移除的依赖
- 动态相关模块
- 用户相关模块
- 评论相关模块
- 直播相关模块
- 桌面小部件
- 性能监控

### 保留的依赖
- 核心网络库 (OkHttp)
- 图片加载 (Glide)
- 视频播放器 (GSYVideoPlayer + Media3)
- UI框架 (Compose + Material3)
- 依赖注入 (Kodein-DI)

## 🔍 后续开发

### 待实现功能
1. **视频详情页** - 完整的视频详情展示
2. **下载进度详情** - 详细的下载状态显示
3. **本地视频播放器** - 集成本地播放功能
4. **搜索历史** - 保存搜索记录
5. **下载设置** - 清晰度、路径等配置

### 优化方向
1. **性能优化** - 减少内存占用
2. **启动速度** - 优化应用启动时间
3. **UI响应** - 提升界面流畅度
4. **网络优化** - 提升下载速度

## 📄 许可证

基于原版 bilimiao2 项目，遵循相同的开源许可证。