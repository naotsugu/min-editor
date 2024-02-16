plugins {
    id("buildlogic.base")
}

dependencies {
    implementation(project(":model-content"))
    implementation(project(":model-edit"))
    implementation(project(":model-text"))
}
