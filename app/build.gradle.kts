plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.getdoc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.getdoc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose Calendar Library
    // implementation("io.github.kizitonwose:compose-calendar:1.0.0")

    // Jetpack Compose Material Components
//    implementation("androidx.compose.material:material:1.7.6")
//    implementation("androidx.compose.ui:ui:1.5.1")
//    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")

    // Firebase Authentication
    implementation (libs.firebase.auth)

    // Firebase Firestore
    implementation (libs.firebase.firestore)

    // Firebase BOM (optional but recommended for version management)
    implementation (platform(libs.firebase.bom))
    implementation (libs.json)

    implementation( libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //Compose Viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

}
