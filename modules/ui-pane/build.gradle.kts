plugins {
    id("code.editor.base")
}

dependencies {
    implementation(project(":javafx"))
    implementation(project(":model-text"))
    implementation(project(":ui-control"))
    implementation(project(":ui-prefs"))
    implementation(project(":ui-model"))
}
