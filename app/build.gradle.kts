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
    namespace = "com.a10miaomiao.bilimiao"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.a10miaomiao.bilimiao"
        minSdk = 21
        targetSdk = 34
        versionCode = 116
        versionName = "2.4.8"

        flavorDimensions += "default"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
            abiFilters.add("armeabi")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
        }
    }

    val signingFile = file("signing.properties")
    if (signingFile.exists()) {
        val props = Properties()
        props.load(FileInputStream(signingFile))
        signingConfigs {
            create("miao") {
                keyAlias = props.getProperty("KEY_ALIAS")
                keyPassword = props.getProperty("KEY_PASSWORD")
                storeFile = file(props.getProperty("KEYSTORE_FILE"))
                storePassword = props.getProperty("KEYSTORE_PASSWORD")
            }
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "bilimiao dev")
            manifestPlaceholders["channel"] = "Development"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfigs.asMap["miao"]?.let {
                signingConfig = it
            }
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    productFlavors {
        create("full") {
            dimension = flavorDimensionList[0]
            val channelName = project.properties["channel"] ?: "Unknown"
            manifestPlaceholders["channel"] = channelName
        }
        create("foss") {
            dimension = flavorDimensionList[0]
            manifestPlaceholders["channel"] = "FOSS"
        }
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
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
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    implementation(Libraries.kotlinxCoroutinesAndroid)
    implementation(Libraries.kodeinDi) // 依赖注入

    implementation(Libraries.recyclerview)
    implementation(Libraries.baseRecyclerViewAdapterHelper)
    implementation(Libraries.swiperefreshlayout)
    implementation(Libraries.foregroundCompat)
    implementation(Libraries.drawer)
    implementation(Libraries.dialogX) {
        exclude("com.github.kongzue.DialogX", "DialogXInterface")
    }
    implementation(Libraries.materialKolor)

//    implementation("com.github.li-xiaojun:XPopup:2.9.13")
//    implementation("com.github.lihangleo2:ShadowLayout:3.2.4")

    implementationSplitties()

    // 播放器相关（已移除）

    implementation(Libraries.okhttp3)
    implementation(Libraries.pbandkRuntime)
    implementation(Libraries.glide)
    annotationProcessor(Libraries.glideCompiler)
    implementation(Libraries.microgSafeParcel)

    implementation(project(":bilimiao-comm"))
    implementation(project(":bilimiao-download"))
    implementation(project(":bilimiao-compose"))

    // 闭源库（已移除）

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.espresso)
}
