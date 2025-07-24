plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.porestaurant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.porestaurant"
        minSdk = 27
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
//them
    implementation (libs.gson)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation (libs.play.services.auth)
    implementation (libs.androidx.activity)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Compose
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.compose.ui:ui:1.8.3")
    implementation("androidx.compose.ui:ui-graphics:1.8.3")
    implementation("androidx.compose.ui:ui-tooling:1.8.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.8.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.3")
    implementation("androidx.compose.material3:material3:1.3.2")

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Networking: Retrofit + Gson + OkHttp-logging
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Charts (MPAndroidChart từ Maven Central)
    implementation(libs.mpandroidchart)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.gson)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Image loading
    implementation(libs.com.github.bumptech.glide.glide)
    annotationProcessor(libs.compiler)

    // Shimmer effect for loading
    implementation(libs.shimmer)

    // Rounded ImageView
    implementation(libs.circleimageview)
}
