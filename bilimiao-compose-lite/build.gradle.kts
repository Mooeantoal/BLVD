import cn.a10miaomiao.bilimiao.build.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("bilimiao-build")
    kotlin("plugin.serialization")
}

android {
    namespace = "cn.a10miaomiao.bilimiao.compose.lite"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    implementation(Libraries.core)
    implementation(Libraries.appcompat)
    implementation(Libraries.material)
    implementation(Libraries.material3)
    implementation(Libraries.lifecycle)
    implementation(Libraries.lifecycleViewModel)
    implementation(Libraries.datastore)

    // Compose 相关
    implementation(Libraries.composeActivity)
    implementation(Libraries.composeNavigation)
    implementation(Libraries.composeUi)
    implementation(Libraries.composeUiTooling)
    implementation(Libraries.composeUiToolingPreview)
    implementation(Libraries.composeMaterial3)
    implementation(Libraries.composeMaterialIconsExtended)
    implementation(Libraries.composeRuntime)

    implementation(Libraries.kotlinxCoroutinesAndroid)
    implementation(Libraries.kodeinDi)
    implementation(Libraries.splittiesActivities)
    implementation(Libraries.splittiesSystemUi)
    implementation(Libraries.splittiesViews)
    implementation(Libraries.splittiesViewBinding)

    implementation(Libraries.glide)
    implementation(Libraries.materialKolor)

    implementationMojito()

    // 精简版只依赖核心模块
    implementation(project(":bilimiao-comm"))
    implementation(project(":bilimiao-download"))

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.espresso)
    androidTestImplementation(Libraries.composeUiTestJunit4)
    debugImplementation(Libraries.composeUiTooling)
    debugImplementation(Libraries.composeUiTestManifest)
}