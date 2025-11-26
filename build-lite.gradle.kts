// 精简版项目构建脚本
// 使用方法: 复制为 build.gradle.kts 并使用 settings-lite.gradle.kts

pluginManagement {
    includeBuild("bilimiao-build")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

rootProject.name = "bilimiao-lite"
include(":app-lite")
include(":bilimiao-comm", ":bilimiao-download")
include(":DanmakuFlameMaster")
include(":bilimiao-compose-lite")