import cn.a10miaomiao.bilimiao.build.*
import java.nio.file.Paths

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("bilimiao-build")
    id("com.google.protobuf")
    kotlin("plugin.serialization")
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        // targetSdk已弃用，使用新的配置方式
        version = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
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
    namespace = "com.a10miaomiao.bilimiao.comm"

    sourceSets {
        getByName("main") {
            // proto配置已移至protobuf块中
        }
    }
}

protobuf {
    val pbandkVersion = Versions.pbandk
    protoc {
        artifact = "com.google.protobuf:protoc:3.12.0"
    }
    plugins {
        create("pbandk") {
            artifact = "pro.streem.pbandk:protoc-gen-pbandk-jvm:$pbandkVersion:jvm8@jar"
        }
    }
    
    // 配置proto源目录
    sourceSets {
        getByName("main").proto.srcDir("src/main/proto")
        getByName("main").proto.include("**/*.proto")
    }
    
    generateProtoTasks {
        val generatorModule = "grpc-generator"
        val generatorClass = "cn.a10miaomiao.generator.GrpcServiceGenerator"
        // grpc-generator/build/libs/grpc-generator.jar
        val generatorJarFile = Paths.get(
            project(":grpc-generator").layout.buildDirectory.get().asFile.path,
            "libs",
            "$generatorModule.jar"
        ).toFile()
        all().forEach { task ->
            task.plugins {
                create("pbandk").apply {
                    if (!generatorJarFile.exists()) {
                        task.dependsOn(":$generatorModule:jar")
                    }
                    option("log=debug")
                    var jarPath = generatorJarFile.path
                    jarPath.indexOf(':')
                        .takeIf { it != -1 }
                        ?.let {
                            // option不能传递`:`符号，故windows情况下只能去除盘符
                            jarPath = jarPath.substring(it + 1, jarPath.length)
                        }
                    option("kotlin_service_gen=${jarPath}|$generatorClass")
                }
            }
        }
    }
}

dependencies {
    implementation(Libraries.core)
    implementation(Libraries.appcompat)
    implementation(Libraries.material)
    implementation(Libraries.datastore)  // 添加DataStore依赖
    implementation(Libraries.lifecycleViewModel)  // 添加ViewModel依赖
    implementation(Libraries.browser)  // 添加browser依赖

    implementation(Libraries.kotlinxSerializationJson)
    implementation(Libraries.kotlinxCoroutinesAndroid)
    implementation(Libraries.kodeinDi)
    implementation(Libraries.glide)
    implementation(Libraries.dialogX) {  // 添加DialogX依赖
        exclude("com.github.kongzue.DialogX", "DialogXInterface")
    }

    implementation(Libraries.okhttp3)
    implementation(Libraries.pbandkRuntime)

    implementation("javax.annotation:javax.annotation-api:1.2")
    
    // 添加DanmakuFlameMaster依赖
    implementation(project(":DanmakuFlameMaster"))

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.espresso)
}