plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }

android {
    namespace = "com.lm.firebaseconnect"
    compileSdk = 33

    defaultConfig { minSdk = 24; targetSdk = 33 }; kotlinOptions { jvmTarget = "1.8" }

    buildFeatures { compose = true }

    composeOptions { kotlinCompilerExtensionVersion = composeCompilerVersion }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //Base
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    //Firebase
    implementation("com.google.firebase:firebase-database-ktx:20.1.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.0")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    //Compose
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:1.4.0-alpha01")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}







