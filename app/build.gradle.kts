plugins {
    id("imagesearch.android.application")
    id("imagesearch.android.compose")
    id("imagesearch.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jg.imagesearch"

    defaultConfig {
        applicationId = "com.jg.imagesearch"
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":feature:main"))
    implementation(project(":feature:bookmark"))
    implementation(project(":feature:search"))
    implementation(project(":feature:viewer"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}