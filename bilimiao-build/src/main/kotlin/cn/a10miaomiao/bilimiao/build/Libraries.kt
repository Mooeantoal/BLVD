package cn.a10miaomiao.bilimiao.build

object Libraries {
    // Kotlin
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:2.0.20"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:2.0.20"
    const val kotlinxCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
    const val kotlinxDatetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.1"
    const val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"

    // AndroidX
    const val core = "androidx.core:core-ktx:1.12.0"
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"
    const val material = "com.google.android.material:material:1.10.0"
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
    const val datastore = "androidx.datastore:datastore-preferences:1.0.0"
    const val media = "androidx.media:media:1.6.0"
    const val browser = "androidx.browser:browser:1.6.0"

    // RecyclerView
    const val recyclerview = "androidx.recyclerview:recyclerview:1.3.2"
    const val baseRecyclerViewAdapterHelper = "io.github.cymchad:BaseRecyclerViewAdapterHelper:4.0.0-beta04"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    const val foregroundCompat = "androidx.core:core:1.12.0"
    const val drawer = "androidx.drawerlayout:drawerlayout:1.2.0"

    // Compose
    const val composeBom = "androidx.compose:compose-bom:2024.02.01"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeFoundation = "androidx.compose.foundation:foundation"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeMaterial3WindowSizeClass = "androidx.compose.material3:material3-window-size-class"
    const val composeMaterial3Adaptive = "androidx.compose.material3:material3-adaptive"
    const val composeMaterialIconsExtended = "androidx.compose.material:material-icons-extended"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val activityCompose = "androidx.activity:activity-compose:1.8.1"
    const val navigationCompose = "androidx.navigation:navigation-compose:2.7.5"

    // Accompanist
    const val accompanistDrawablePainter = "com.google.accompanist:accompanist-drawablepainter:0.32.0"
    const val accompanistAdaptive = "com.google.accompanist:accompanist-adaptive:0.32.0"

    // Third-party
    const val dialogX = "com.github.kongzue.DialogX:DialogX:0.0.48.beta19"
    const val materialKolor = "com.github.duanhong169:colorpicker:1.1.6"
    const val kodeinDi = "org.kodein.di:kodein-di:7.21.1"
    const val kodeinDiCompose = "org.kodein.di:kodein-di-framework-compose:7.21.1"

    // Network
    const val okhttp3 = "com.squareup.okhttp3:okhttp:4.12.0"

    // Protobuf
    const val pbandkRuntime = "pro.streem.pbandk:pbandk-runtime:0.15.1"

    // Image
    const val glide = "com.github.bumptech.glide:glide:4.16.0"
    const val glideCompiler = "com.github.bumptech.glide:compiler:4.16.0"
    const val glideCompose = "com.github.bumptech.glide:compose:1.0.0-alpha.1"
    const val qrose = "com.github.alexzhirkevich:custom-qr-generator:1.6.2"

    // MicroG
    const val microgSafeParcel = "com.microg:safe-parcel:1.8.0"

    // Testing
    const val junit = "junit:junit:4.13.2"
    const val androidxJunit = "androidx.test.ext:junit:1.1.5"
    const val espresso = "androidx.test.espresso:espresso-core:3.5.1"
}

fun Project.implementationSplitties() {
    val splittiesVersion = "3.0.0"
    dependencies.add("implementation", "com.louiscad.splitties:splitties-activities:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-alertdialog-appcompat:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-collections:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-dimensions:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-exceptions:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-fun-packs-android-appcompat-with-views-dsl:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-fun-packs-android-base-with-views-dsl:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-init:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-lifecycle:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-permissions:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-preferences:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-resources:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-systemservices:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-toast:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-typedbundles:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-views-dsl:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-views-dsl-appcompat:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-views-dsl-constraintlayout:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-views-dsl-material:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-views-dsl-recyclerview:$splittiesVersion")
    dependencies.add("implementation", "com.louiscad.splitties:splitties-views-recyclerview:$splittiesVersion")
}