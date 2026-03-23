plugins {
    id("imagesearch.android.library")
    id("imagesearch.android.compose")
    id("imagesearch.android.hilt")
}

android {
    namespace = "com.jg.imagesearch.feature.viewer"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(libs.coil.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
