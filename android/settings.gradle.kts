pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version "8.4.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.22" apply false
        id("com.google.dagger.hilt.android") version "2.48.1" apply false
        id("kotlinx-serialization") version "1.6.2" apply false
        id("com.google.gms.google-services") version "4.4.1" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "MoneyTracker"
include(":app")