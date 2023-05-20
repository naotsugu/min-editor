plugins {
    id("code.editor.base")
}

dependencies {
    implementation(project(":model-core"))
    implementation(project(":model-text"))
    implementation(project(":model-edit"))
    implementation(project(":model-style"))
    implementation(project(":piecetable"))
}
