plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vertcdemo.solution.interactivelive"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "APP_VERSION_NAME", "\"${extra["APP_VERSION_NAME"]}\"")

        buildConfigField("String", "LIVE_TTSDK_APP_NAME", "\"${extra["LIVE_TTSDK_APP_NAME"]}\"")
        buildConfigField("String", "LIVE_TTSDK_APP_ID", "\"${extra["LIVE_TTSDK_APP_ID"]}\"")
        buildConfigField(
            "String",
            "LIVE_TTSDK_LICENSE_URI",
            "\"${extra["LIVE_TTSDK_LICENSE_URI"]}\""
        )
        buildConfigField(
            "String",
            "LIVE_TTSDK_APP_CHANNEL",
            "\"${extra["LIVE_TTSDK_APP_CHANNEL"]}\""
        )

        buildConfigField("String", "LIVE_PULL_DOMAIN", "\"${extra["LIVE_PULL_DOMAIN"]}\"")
        buildConfigField("String", "LIVE_PUSH_DOMAIN", "\"${extra["LIVE_PUSH_DOMAIN"]}\"")
        buildConfigField("String", "LIVE_PUSH_KEY", "\"${extra["LIVE_PUSH_KEY"]}\"")
        buildConfigField("String", "LIVE_APP_NAME", "\"${extra["LIVE_APP_NAME"]}\"")
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

    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.material3)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.constraintlayout.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.retrofit2)

    implementation(libs.accompanist.permissions)

    implementation(libs.bundles.rtc.sdk)

    implementation(libs.eventbus)

    implementation(project(":components:base"))
}
