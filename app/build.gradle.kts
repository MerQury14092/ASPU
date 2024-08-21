plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.merqury.aspu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.merqury.aspu"
        minSdk = 28
        versionCode = 18
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation("com.github.raheemadamboev:check-internet-android:1.1.1")
    implementation("com.canopas.intro-showcase-view:introshowcaseview:2.0.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.squareup.okhttp:okhttp:2.7.5")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("org.jetbrains:markdown:0.6.1")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation("com.airbnb.android:lottie-compose:6.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.accompanist:accompanist-placeholder:0.35.0-alpha")
    implementation("androidx.compose.material:material")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation("net.engawapg.lib:zoomable:1.6.0")
    implementation("com.google.accompanist:accompanist-webview:0.35.0-alpha")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.1.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.0"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc02")
    implementation("com.google.android.material:material:1.12.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}