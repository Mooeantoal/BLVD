import cn.a10miaomiao.bilimiao.build.*
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    id("kotlin-android")
    id("bilimiao-build")
}

android {
    namespace = "com.a10miaomiao.bilimiao.lite"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.a10miaomiao.bilimiao.lite"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-lite"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "bilimiao lite dev")
            manifestPlaceholders["channel"] = "Development"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation(Libraries.core)
    implementation(Libraries.appcompat)
    implementation(Libraries.material)
    implementation(Libraries.lifecycle)
    implementation(Libraries.lifecycleViewModel)
    implementation(Libraries.datastore)
    implementation(Libraries.media)
    implementation(Libraries.browser)

    implementation(Libraries.kotlinxCoroutinesAndroid)
    implementation(Libraries.kodeinDi)

    implementation(Libraries.recyclerview)
    implementation(Libraries.baseRecyclerViewAdapterHelper)
    implementation(Libraries.swiperefreshlayout)
    implementation(Libraries.foregroundCompat)
    implementation(Libraries.drawer)
    implementation(Libraries.dialogX) {
        exclude("com.github.kongzue.DialogX", "DialogXInterface")
    }
    implementation(Libraries.materialKolor)

    implementationSplitties()
    implementationMojito()

    // 播放器相关
    implementation(Libraries.media3)
    implementation(Libraries.media3Session)
    implementation(Libraries.media3Decoder)
    implementation(Libraries.media3Ui)
    implementation(Libraries.media3ExoPlayer)
    implementation(Libraries.media3ExoPlayerDash)
    implementation(Libraries.gsyVideoPlayer)

    implementation(Libraries.okhttp3)
    implementation(Libraries.pbandkRuntime)
    implementation(Libraries.glide)
    annotationProcessor(Libraries.glideCompiler)

    // 精简版依赖
    implementation(project(":bilimiao-comm"))
    implementation(project(":bilimiao-download"))
    implementation(project(":bilimiao-compose"))
    implementation(project(":DanmakuFlameMaster"))

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.espresso)
}