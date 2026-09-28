# ProGuard rules for MoneyTracker

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keep class * extends dagger.hilt.android.HiltActivity { *; }
-keep class * extends dagger.hilt.android.HiltFragment { *; }
-keep class * extends dagger.hilt.android.HiltViewModel { *; }

# Keep Room entities and DAOs
-keep class com.moneytracker.data.local.entity.** { *; }
-keep class com.moneytracker.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep serialization classes
-keep class kotlinx.serialization.** { *; }
-keep class com.moneytracker.domain.model.** { *; }
-keep class com.moneytracker.data.remote.dto.** { *; }

# Keep Retrofit interfaces
-keep interface com.moneytracker.core.network.api.** { *; }

# Keep WorkManager workers
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# Keep Biometric/Crypto classes
-keep class androidx.biometric.** { *; }
-keep class androidx.security.crypto.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }

# Keep Sentry
-keep class io.sentry.** { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Moshi
-keep class com.squareup.moshi.** { *; }

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Play Integrity
-keep class com.google.android.play.core.integrity.** { *; }

# Optimize
-optimizationpasses 5
-allowaccessmodification
-dontskipnonpubliclibraryclassmembers

# Obfuscation
-useuniqueclassmembernames
-adaptresourcefilenames **.properties,**.xml,**.png,**.jpg,**.jpeg,**.gif
-adaptresourcefilecontents **.properties,**.xml

# Keep annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# Keep Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}