plugins {
    id("imagesearch.android.library")
    id("imagesearch.android.hilt")
    id("imagesearch.android.network")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jg.imagesearch.core.network"
}

dependencies {
    implementation(project(":core:model"))

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
}
