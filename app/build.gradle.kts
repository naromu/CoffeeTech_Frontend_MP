plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services) // Cambio aquí
}

android {
    namespace = "com.example.coffetech"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.coffetech"
        minSdk = 24
        targetSdk = 34
        versionCode = 4 // Este es el código de versión
        versionName = "4.1.3"  // Este es el nombre de la versión

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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.gson)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.appcompat.resources)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.maps.android:maps-compose:2.8.0")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("com.github.tehras:charts:0.2.2-alpha")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // Verifica la última versión

    implementation("androidx.room:room-ktx:2.5.2")

    implementation("io.coil-kt:coil-compose:2.4.0")




    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")
}