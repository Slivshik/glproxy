// WLRUS root build.gradle.kts
// AGP 8.8.x is the first version compatible with Gradle 9.x
// Kotlin 2.0.x is required for AGP 8.7+

plugins {
    id("com.android.application")      version "8.8.2" apply false
    id("com.android.library")          version "8.8.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.kapt")    version "2.0.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
