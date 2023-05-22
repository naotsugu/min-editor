plugins {
    id("code.editor.base")
}

dependencies {
    api(project(":model-core"))
    api(project(":model-style"))
    implementation(project(":model-text"))
    implementation(project(":model-edit"))
    implementation(project(":piecetable"))
}
