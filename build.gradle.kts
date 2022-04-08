// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("gradle_version", "7.1.3")
        set("kotlin_version", "1.6.10")
        set("nav_version", "2.4.1")
    }
    // ext.and_server_version = "2.1.9"
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${rootProject.extra["gradle_version"]}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlin_version"]}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${rootProject.extra["nav_version"]}")
        // classpath "com.yanzhenjie.andserver:plugin:$and_server_version"
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}