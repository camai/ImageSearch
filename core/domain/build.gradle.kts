plugins {
    id("imagesearch.android.library")
}

android {
    namespace = "com.jg.imagesearch.core.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.paging.runtime)
    implementation(libs.kotlinx.serialization.json)
}
