plugins {
    id("imagesearch.android.library")
    id("imagesearch.android.hilt")
}

android {
    namespace = "com.jg.imagesearch.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // Room (for withTransaction)
    implementation(libs.room.ktx)

    // Paging
    implementation(libs.paging.runtime)
}
