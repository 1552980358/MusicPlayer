plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "projekt.cloud.piece.music.player"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        @Suppress("UNUSED_VARIABLE")
        val release by getting {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
        @Suppress("UNUSED_VARIABLE")
        val debug by getting {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    lint.abortOnError = false
}

dependencies {

    //noinspection GradleDependency,DifferentStdlibGradleVersion
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.navigation:navigation-fragment-ktx:${rootProject.extra["nav_version"]}")
    implementation("androidx.navigation:navigation-ui-ktx:${rootProject.extra["nav_version"]}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.media:media:1.5.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.preference:preference-ktx:1.2.0")
    kapt("androidx.databinding:databinding-compiler-common:${rootProject.extra["gradle_version"]}")
    val roomVersion = "2.4.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.viewpager:viewpager:1.1.0-alpha01")

    implementation("com.google.android.material:material:1.5.0")
    val exoPlayerVersion = "2.16.1"
    implementation("com.google.android.exoplayer:exoplayer-core:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-hls:$exoPlayerVersion")
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.github.android:renderscript-intrinsics-replacement-toolkit:b6363490c3")

    implementation("com.github.1552980358:KtExtension:1.2.5")
    implementation("com.github.1552980358:KtExtensionAndroid:1.2.10")
    implementation("com.github.1552980358:C2Pinyin:1.0.0")

    implementation("com.github.mkaflowski:Media-Style-Palette:1.3")

    // Not working
    // implementation "com.yanzhenjie.andserver:api:${and_server_version}"
    // kapt "com.yanzhenjie.andserver:processor:${and_server_version}"

    val nano_httpd_version = "2.3.1"
    implementation("org.nanohttpd:nanohttpd:$nano_httpd_version")
    implementation("org.nanohttpd:nanohttpd-websocket:$nano_httpd_version")

    implementation("com.github.thegrizzlylabs:sardine-android:v0.8")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}