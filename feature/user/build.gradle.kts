plugins {
    id(Libs.plugin.library)
}

android("com.nefrit.users")

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreData)
    implementation(projects.coreUi)

    testImplementation(*Libs.bundle.unitTests)
}