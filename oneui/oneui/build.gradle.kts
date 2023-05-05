plugins {
    id("com.android.library")
    kotlin("android")
    id("dev.rikka.tools.refine")
}

android {
    namespace = "io.mesalabs.oneui"
    compileSdk = 33

    defaultConfig {
        minSdk = 30
    }

    buildFeatures {
        buildConfig = false
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

configurations.all {
    exclude(group = "androidx.appcompat", module = "appcompat")
    exclude(group = "androidx.core", module = "core")
    exclude(group = "androidx.customview", module = "customview")
    exclude(group = "androidx.fragment", module = "fragment")
}

dependencies {
    // Sesl: https://github.com/OneUIProject/oneui-core/tree/sesl4
    implementation("io.github.oneuiproject.sesl:appcompat:1.3.0")
    implementation("io.github.oneuiproject.sesl:material:1.4.0") {
        exclude(group = "io.github.oneuiproject.sesl", module = "viewpager2")
    }
    // AndroidX: https://developer.android.com/jetpack/androidx/versions
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0") {
        exclude(group = "androidx.recyclerview", module = "recyclerview")
    }
    // HiddenApiRefinePlugin: https://github.com/RikkaApps/HiddenApiRefinePlugin
    implementation("dev.rikka.tools.refine:runtime:4.3.0")

    compileOnly(project(":oneui:stub"))
}