plugins {
    id("imagesearch.android.library")
    id("imagesearch.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jg.imagesearch.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Paging
    implementation(libs.paging.runtime)
}
