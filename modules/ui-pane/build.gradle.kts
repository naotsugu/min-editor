plugins {
    id("code.editor.base")
}

dependencies {
    implementation(project(":javafx"))
    implementation(project(":model-editor"))
    implementation(project(":model-buffer"))
    implementation(project(":model-text"))
    implementation(project(":model-edit"))
    implementation(project(":model-layout-fx"))
    implementation(project(":ui-control"))
}
