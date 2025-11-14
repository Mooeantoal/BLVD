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
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") //pbandk
    }
}
rootProject.name = "bilimiao"
include(":app")
include(":bilimiao-download")
include(":bilimiao-comm")
