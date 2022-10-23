plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
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
            buildConfigField(
                "String",
                "FCM_SERVER_KEY",
                "\"AAAAoyEPC5o:APA91bFan2FPNVGsLjebfDbm51TUz0-KPhcl86TZe9CwyYoOmTr631B5Axd7eRJ3qfg5PUC4SAKCJkndfmPCf2rq7fl9X1xzkFsitgiqQbQq4gtRHAc3keGyKoIs1O4TzNPSdgBT5HbK\""
            )
            buildConfigField("String", "C_KEY", "\"jfdjga879coaerhd\"")
        }
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
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0-rc01")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.1-alpha")
    implementation(project(mapOf("path" to ":firebaseConnect")))
}