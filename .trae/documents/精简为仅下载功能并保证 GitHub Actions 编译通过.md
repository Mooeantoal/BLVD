## 目标与范围
- 仅保留“下载”相关能力：下载队列、弹幕拉取、播放地址解析与媒体文件落地。
- 移除与播放、封面预览、消息/社交、排行榜、设置等无关功能与依赖。
- 统一 SDK/插件版本，简化构建与 CI，确保在 GitHub Actions 上稳定编译通过。

## 保留与移除的模块
- 保留模块：`app`、`bilimiao-download`、`bilimiao-comm`、`grpc-generator`、`DanmakuFlameMaster`（被 comm/download 引用）。
- 暂时保留模块（Phase 1 为避免大规模改动）：`bilimiao-compose`（仅用于展示下载列表/详情/创建页）。
- 移除模块：`benchmark`、`bilimiao-cover`、`bilimiao-appwidget`。后续（Phase 2）可进一步删除 compose 中的非下载页面源码。

## 设置与依赖统一
- 统一 `compileSdk/targetSdk` 到 34（适配 compose/comm），避免 32/34/35 混用导致的 Toolchain 不一致。
  - 修改：
    - `app/build.gradle.kts:13-20` 下的 `compileSdk/targetSdk` 调整为 34。
    - `bilimiao-download/build.gradle.kts:12-17` 仍为 32 → 上调到 34。
    - `bilimiao-comm/build.gradle.kts:15-20` 已为 34 → 保持。
    - `bilimiao-compose/build.gradle.kts:13-18` 已为 34 → 保持。
- 如需降低统一到 33 亦可，但 34 更贴近期依赖版本矩阵（`compose-bom 2025.05.00`）。

## Gradle 配置与依赖精简
- 根 `settings.gradle.kts:21-24`：仅包含必要模块，去除 `:benchmark`、`:bilimiao-cover`、`bilimiao-appwidget`；保留 `:grpc-generator` 与 `:DanmakuFlameMaster` 以满足 proto 生成与少量依赖。
- `app/build.gradle.kts` 依赖裁剪：
  - 删除播放器相关依赖：`media3*`、`gsyVideoPlayer`（行 142-151）。
  - 删除图片预览与封面：`implementationMojito()`、`project(":bilimiao-cover")`（行 139-141、159）。
  - 保留 `:bilimiao-comm` 与 `:bilimiao-download`、`:bilimiao-compose`（行 157-162）。如后续完全去除 compose，则在 Phase 2 再删。
  - 移除闭源/渠道特定依赖：`baiduMobstat`、`sensebot`、`lib-decoder-av1-release.aar`（行 165-170）。
- `bilimiao-compose/build.gradle.kts`：
  - Phase 1 保持依赖以快速通过编译；Phase 2 删除与非下载页面相关的依赖与源码（如 message、rank、user、dynamic 等页面），同时移除 `project(":bilimiao-cover")`（行 98-101），并删除引用该模块的页面文件。
- `bilimiao-download/build.gradle.kts`：
  - 保留 `okhttp3`、`serialization`、`pbandkRuntime` 与 `project(":bilimiao-comm")`（行 41-49）。
  - 可评估移除 `project(":DanmakuFlameMaster")`（行 50）——下载模块当前未直接使用该 API，若移除需确保 `bilimiao-comm` 的引用未传递到下载路径中。

## 应用清理（Manifest 与入口）
- `app/src/main/AndroidManifest.xml`：
  - 保留权限：`INTERNET`、`ACCESS_NETWORK_STATE`、`POST_NOTIFICATIONS`、`FOREGROUND_SERVICE`（下载进度通知/前台服务）。
  - 移除与播放/封面相关组件：
    - 删除 `PlaybackService` 声明（行 80-88）。
    - 删除 `CoverActivity`（行 64-75）与 `LogViewerActivity`/`DensitySettingActivity` 等非下载必要 Activity（行 56-63）。
  - 保留 `MainActivity` 与 `cn.a10miaomiao.bilimiao.download.DownloadService`（行 77-79）。
- `MainActivity.kt` 与 `MainUi.kt`：
  - Phase 1：
    - 删除/禁用播放器委托与相关 UI（如 `DanmakuVideoPlayer`、`PlayerDelegate2`、`PlayerBehavior` 等引用），保留一个最小 UI 容器。
    - 默认导航到 `DownloadListPage`，通过 `StartViewWrapper` 的菜单或直接跳转 `bilimiao://download`。
  - Phase 2（可选）：将入口精简为单 Activity + Fragment（或 Compose Activity）仅承载下载列表与创建页，移除 `StartViewWrapper`、抽屉与多内容区逻辑。

## 下载功能保持与验证
- 下载主逻辑（保留不动）：
  - `bilimiao-download/src/main/java/.../DownloadService.kt`（弹幕 XML 下载：`CompressionTools`；播放地址：`BiliPalyUrlHelper.playUrl`；媒体写入：`DownloadManager`）
    - 参考：`bilimiao-download/src/main/java/cn/a10miaomiao/bilimiao/download/DownloadService.kt:217-236`、`:250-320`、`:519-550`。
  - 依赖 `bilimiao-comm`：网络请求 `MiaoHttp` 与 JSON 解析 `MiaoJson`。
- UI 路径（Phase 1 保留）：
  - `bilimiao-compose/pages/download/DownloadListPage.kt`、`DownloadDetailPage.kt`、`DownloadBangumiCreatePage.kt` 与 `VideoDownloadDialog.kt`。

## GitHub Actions 调整
- `.github/workflows/android.yml`：
  - 触发条件：改为 `push`/`pull_request`（便于持续验证），或保留 tag 触发但用于 debug 构建。
  - 取消签名步骤（行 22-29），改为普通编译：
    - `Build with Gradle`：使用 `./gradlew app:assembleDebug`（不依赖签名与闭源渠道包）。
  - 如需发布 APK，可将 `debug` 构建产物上传 Artifact，而非 Release；或后续为 Release 添加一个默认 debug 签名配置。
  - 保留 JDK17 与 Gradle 缓存。

## 验证与交付
- 本地验证：
  - `./gradlew clean app:assembleDebug` 与 `:bilimiao-download:assembleRelease`（库编译验证）。
  - 确认 `DownloadService` 在 `Android 13+` 通知权限下能正常发出前台通知（需要设备测试；CI 仅编译）。
- CI 验证：
  - 推送后 GitHub Actions 成功执行 `assembleDebug` 并产出 APK；Artifacts 可在 Job 中下载。

## 分阶段实施
- Phase 1（优先保证编译与功能）：
  - 调整 `settings.gradle.kts` 模块集合。
  - 精简 `app` 依赖与 Manifest；默认路由到下载页面。
  - 更新 CI 工作流为 Debug 构建，无签名。
  - 统一 SDK 为 34。
- Phase 2（进一步瘦身）：
  - 删除 compose 中非下载页面源码与相关依赖；如删除 `bilimiao-cover` 及其引用。
  - 评估移除 `DanmakuFlameMaster` 依赖链（若下载路径未使用）。

## 变更影响与风险控制
- 网络层 `MiaoHttp` 依赖 `BilimiaoCommApp` 的 cookie/buvid，实际功能仍需登录与 Cookie 环境；但编译与安装不会受影响。
- 移除播放器/封面后，可能存在少量残余引用；Phase 1 将通过编译错误逐步清理这些引用。
- 版本统一可能影响旧设备支持范围；`minSdk 21` 保持不变。