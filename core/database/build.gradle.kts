plugins {
    id("imagesearch.android.library")
    id("imagesearch.android.hilt")
}

android {
    namespace = "com.jg.imagesearch.core.database"
}

dependencies {
    implementation(project(":core:model"))

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Paging
    implementation(libs.paging.runtime)
}
