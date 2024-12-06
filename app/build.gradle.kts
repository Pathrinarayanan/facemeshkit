plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.undercouch.download)
}

android {

    namespace = "com.example.faceland"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.faceland"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding =true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

//apply from: 'download_tasks.gradle'
//apply {
//
//} "$rootDir/download_tasks.gradle

}


//ext {
//    ASSET_DIR = file("$rootDir/src/main/assets")
//}
//ext {
//    ASSET_DIR = projectDir.toString() + '/src/main/assets'
//}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.camera.lifecycle)
    testImplementation(libs.junit)
    implementation(libs.mediapipe)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.camerax)
    implementation(libs.androidx.camera.view)
    implementation(libs.removebg)
    implementation(libs.androidx.camera.camera2)
    androidTestImplementation(libs.androidx.espresso.core)
}