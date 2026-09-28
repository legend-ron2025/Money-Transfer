plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.moneytracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moneytracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packagingOptions {
        resources {
            excludes += listOf("META-INF/*")
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // Core Android
    val coreKtx = "1.12.0"
    val appCompat = "1.6.1"
    val material3 = "1.2.1"
    val activityCompose = "1.8.2"
    val lifecycle = "2.7.0"
    val room = "2.6.1"
    val navigation = "2.7.6"
    val hilt = "2.48.1"
    val retrofit = "2.11.0"
    val okhttp = "4.12.0"
    val moshi = "1.15.1"
    val coil = "2.5.0"
    val coroutines = "1.7.3"
    val flow = "1.0.0"
    val datastore = "1.1.1"
    val biometric = "1.2.0-alpha04"
    val crypto = "1.1.0-alpha06"
    val workManager = "2.9.0"
    val playIntegrity = "1.2.1"
    val firebaseAuth = "22.3.0"
    val firebaseMessaging = "23.4.0"
    val firebaseCrashlytics = "18.6.0"
    val firebaseAnalytics = "21.5.0"
    val firebasePerf = "20.5.1"
    val sentry = "7.10.0"

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")

    implementation(composeBom)
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.appcompat:appcompat:$appCompat")
    implementation("androidx.compose.material3:material3:$material3")
    implementation("androidx.activity:activity-compose:$activityCompose")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$navigation")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigation")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:$hilt")
    kapt("com.google.dagger:hilt-compiler:$hilt")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // Room Database
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.okhttp3:okhttp:$okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")

    // Image Loading
    implementation("io.coil-kt:coil-compose:$coil")

    // Coroutines & Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:$datastore")
    implementation("androidx.datastore:datastore-preferences-core:$datastore")

    // Biometric & Crypto
    implementation("androidx.biometric:biometric:$biometric")
    implementation("androidx.security:security-crypto-ktx:$crypto")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:$workManager")

    // Play Integrity
    implementation("com.google.android.play:integrity:$playIntegrity")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    // Sentry
    implementation("io.sentry:sentry-android:$sentry")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.room:room-testing:$room")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.6.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")

    // Debug
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
}

kapt {
    correctErrorTypes = true
    javacOptions {
        option("-Xlint:unchecked")
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }
}