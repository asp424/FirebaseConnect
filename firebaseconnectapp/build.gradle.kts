plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.lm.firebaseconnectapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.lm.firebaseconnectapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            //  isMinifyEnabled = true
            buildConfigField(
                "String",
                "FCM_SERVER_KEY",
                "\"AAAAoyEPC5o:APA91bFan2FPNVGsLjebfDbm51TUz0-KPhcl86TZe9CwyYoOmTr631B5Axd7eRJ3qfg5PUC4SAKCJkndfmPCf2rq7fl9X1xzkFsitgiqQbQq4gtRHAc3keGyKoIs1O4TzNPSdgBT5HbK\""
            )
            buildConfigField("String", "C_KEY", "\"jfdjga879coaerhd\"")
            buildConfigField(
                "String",
                "WEB_CLIENT_ID",
                "\"700634303386-0lerrkifeeaqrkujudnspt6b7f7oam76.apps.googleusercontent.com\""
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "FCM_SERVER_KEY",
                "\"AAAAoyEPC5o:APA91bFan2FPNVGsLjebfDbm51TUz0-KPhcl86TZe9CwyYoOmTr631B5Axd7eRJ3qfg5PUC4SAKCJkndfmPCf2rq7fl9X1xzkFsitgiqQbQq4gtRHAc3keGyKoIs1O4TzNPSdgBT5HbK\""
            )
            buildConfigField("String", "C_KEY", "\"jfdjga879coaerhd\"")
            buildConfigField(
                "String",
                "WEB_CLIENT_ID",
                "\"700634303386-0lerrkifeeaqrkujudnspt6b7f7oam76.apps.googleusercontent.com\""
            )
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "33.0.0"
}

dependencies {
//Base
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    //JitsiMeet
    // implementation("org.jitsi.react:jitsi-meet-sdk:+") { isTransitive = true }

    //FirebaseConnect
    implementation(project(mapOf("path" to ":firebaseConnect")))

    //Firebase
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    //Compose
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:1.4.0-alpha02")
    implementation("androidx.compose.material:material-icons-core:1.3.1")
    implementation("androidx.compose.material:material-icons-extended:1.3.1")
    implementation("androidx.compose.material3:material3:1.1.0-alpha02")
    implementation("androidx.compose.material:material:1.4.0-alpha02")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.1-alpha")

    //Dagger
    implementation("com.google.dagger:dagger:2.44.2")
    kapt("com.google.dagger:dagger-compiler:2.44.2")

    //Auth
    implementation("com.google.android.gms:play-services-auth:20.4.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")

    //ColorPicker
    implementation("com.godaddy.android.colorpicker:compose-color-picker:0.5.0")

    //Leak Canary
    // debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.9.1")

    //Visualizer
    implementation("io.github.gautamchibde:audiovisualizer:2.2.5")

    //Progress
    implementation(files("libs/fantasticprogress-release.aar"))
}
