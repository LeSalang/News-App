plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)

    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.lesa.newsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lesa.newsapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildFeatures {
            buildConfig = true
        }

        buildConfigField("String", "NEWS_API_KEY", "\"5d937dfc9bfc4a3fa0237a8b76193c0e\"")
        buildConfigField("String", "NEWS_API_BASE_URL", "\"https://newsapi.org/v2/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.dagger.hilt.android)

    debugImplementation(libs.okhttp.logging.interceptor)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.converter)

    implementation(platform(libs.androidx.compose.bom))
    kapt(libs.dagger.hilt.compiler)

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":data"))
    implementation(project(":database"))
    implementation(project(":features:main"))
    implementation(project(":uikit"))
}
