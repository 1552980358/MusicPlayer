plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
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
            applicationIdSuffix = ".debug"
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
    
    kapt {
        generateStubs = true
    }
    
    lint.abortOnError = false
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    
    val roomVersion = "2.4.2"
    implementation( "androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    
    val exoPlayerVersion = "2.18.1"
    implementation("com.google.android.exoplayer:exoplayer-core:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-hls:$exoPlayerVersion")
    implementation("com.google.android.material:material:1.7.0-alpha03")
    
    implementation("com.github.1552980358:C2Pinyin:2.3.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}