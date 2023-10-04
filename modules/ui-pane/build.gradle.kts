plugins {
    id("code.editor.base")
}

dependencies {

    implementation(project(":javafx"))

    implementation(project(":model-buffer"))
    implementation(project(":model-text"))
    implementation(project(":model-layout"))
    implementation(project(":model-edit"))
    implementation(project(":model-layout-fx"))
    implementation(project(":syntax"))
    implementation(project(":ui-control"))
    implementation(project(":ui-prefs"))
    implementation(project(":ui-model"))
}
