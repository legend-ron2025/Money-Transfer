// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("kotlinx-serialization") version "1.6.2" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

allprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-Xopt-in=androidx.lifecycle.ExperimentalLifecycleApi"
            )
        }
    }
}