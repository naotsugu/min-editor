plugins {
    id("code.editor.base")
}

dependencies {
    implementation("com.mammb.code:javafx")
    implementation(project(":model-layout-fx"))
    implementation(project(":model-editor"))
}
