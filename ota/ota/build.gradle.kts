plugins {
    id("com.android.library")
    id("dev.rikka.tools.refine")
}

android {
    namespace = "org.lineageos.updater"
    compileSdk = 33

    defaultConfig {
        minSdk = 30
    }

    lint {
        disable += "ProtectedPermissions"
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation("io.github.oneuiproject.sesl:appcompat:1.4.0")
    implementation("io.github.oneuiproject.sesl:material:1.5.0") {
        exclude(group = "io.github.oneuiproject.sesl", module = "viewpager2")
    }
    implementation("io.github.oneuiproject.sesl:preference:1.1.0")
    // AndroidX: https://developer.android.com/jetpack/androidx/versions
    implementation("androidx.viewpager2:viewpager2:1.0.0") {
        exclude(group = "androidx.recyclerview", module = "recyclerview")
    }
    // HiddenApiRefinePlugin: https://github.com/RikkaApps/HiddenApiRefinePlugin
    implementation("dev.rikka.tools.refine:runtime:4.3.0")

    compileOnly(project(":ota:stub"))
}
