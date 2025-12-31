plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.enjoytime"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.enjoytime"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.okhttp)
    implementation(libs.gson)
    // 高德地图SDK和定位服务
    // 高德地图SDK（本地jar包）
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // 高德定位SDK 6.5.1
    implementation("com.amap.api:location:6.5.1")
    // 高德搜索服务（可选）
    // implementation("com.amap.api:search:9.7.4")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}