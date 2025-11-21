# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Compose + immutable collection warnings
-dontwarn kotlinx.collections.immutable.**

# Keep Application entry point so dependency injection / WorkManager can reflectively resolve it.
-keep class com.productivitystreak.NeverZeroApplication { *; }

# Moshi uses reflection for adapter lookup. Keep public models to prevent stripping JSON fields.
-keep class com.productivitystreak.data.model.** { *; }

# WorkManager reflects on Worker subclasses; ensure constructors stay intact.
-keep class com.productivitystreak.notifications.** extends androidx.work.ListenableWorker { *; }

# Optional: keep coroutines debug probes when running with JVM debug options.
-keep class kotlinx.coroutines.debug.internal.DebugProbesImpl { *; }
-keep class kotlinx.coroutines.DebugProbesKt { *; }
