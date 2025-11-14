plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile>().all {
    enabled = false
}

// 添加jar任务配置
tasks.jar {
    archiveBaseName.set("grpc-generator")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

dependencies {
    val pbandkVersion = "0.14.4-SNAPSHOT"
    compileOnly("pro.streem.pbandk:pbandk-runtime:$pbandkVersion")
    compileOnly("pro.streem.pbandk:protoc-gen-pbandk-lib:$pbandkVersion")
}