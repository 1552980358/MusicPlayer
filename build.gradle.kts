// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("gradleVersion", "7.2.0")
        set("kotlinVersion", "1.6.21")
        set("navVersion", "2.4.2")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${rootProject.extra["gradleVersion"]}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlinVersion"]}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${rootProject.extra["navVersion"]}")
    }
}

plugins {
    id("com.android.application") version "7.1.3" apply false
    id("com.android.library") version "7.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    id("com.google.devtools.ksp") version "1.6.21-1.0.5" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}