plugins {
    id("com.android.library")
}

android {
    namespace = "org.lineageos.updater.stub"
    compileSdk = 33

    defaultConfig {
        minSdk = 30
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    // HiddenApiRefinePlugin: https://github.com/RikkaApps/HiddenApiRefinePlugin
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:4.3.0")
    compileOnly("dev.rikka.tools.refine:annotation:4.3.0")
    // AndroidX: https://developer.android.com/jetpack/androidx/versions
    implementation("androidx.annotation:annotation:1.6.0")
}
