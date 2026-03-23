plugins {
    id("imagesearch.android.library")
    id("imagesearch.android.hilt")
}

android {
    namespace = "com.jg.imagesearch.core.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.paging.runtime)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.paging.testing)
}
