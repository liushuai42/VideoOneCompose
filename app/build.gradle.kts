plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vertcdemo.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "me.liushuai42.compose.videoone"

        minSdk = libs.versions.minSdk.toInt()
        targetSdk = libs.versions.targetSdk.toInt()

        versionCode = (extra["APP_VERSION_CODE"] as String).toInt()
        versionName = extra["APP_VERSION_NAME"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "VideoOneCompose-$versionName-$versionCode")

        buildConfigField(
            "String",
            "GITHUB_REPO",
            "\"${extra["GITHUB_REPO"]}\""
        )
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
    kotlin {
        jvmToolchain(11)
    }
    buildFeatures {
        buildConfig = true
        compose = true
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

    implementation(libs.bundles.androidx.material3)
    implementation(libs.bundles.androidx.lifecycle)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.constraintlayout.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":components:base"))
    implementation(project(":components:login"))

    implementation(project(":solutions:interactivelive"))
}

private fun Provider<String>.toInt() = get().toInt()