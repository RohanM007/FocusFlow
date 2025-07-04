plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.opscpoe.foucsflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.opscpoe.foucsflow"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {



    implementation ("androidx.work:work-runtime-ktx:2.8.1")

    // Splash animation
    implementation("com.airbnb.android:lottie:4.2.0")
    implementation ("com.airbnb.android:lottie:lottieVersion")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    // Web APIs (Retrofit, Gson, and Glide)
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))  // Firebase BOM
    implementation("com.google.firebase:firebase-auth-ktx")  // Firebase Authentication KTX
    implementation("com.google.firebase:firebase-firestore:25.1.0")  // Firestore

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // AndroidX Libraries
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")
    implementation("com.google.firebase:firebase-messaging:24.0.2")
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Graphs
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")

    //biometrics
    implementation("androidx.biometric:biometric:1.2.0-alpha04")

    // Room components
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    // Coroutine support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

// LiveData components
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")
}
