// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.0" apply false
    id("com.android.library") version "7.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.devtools.ksp") version "1.7.21-1.0.8" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.0" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}