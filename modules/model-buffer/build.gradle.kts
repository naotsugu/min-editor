plugins {
    id("code.editor.base")
}

dependencies {
    api(project(":model-core"))
    api(project(":model-style"))
    api(project(":model-edit"))
    implementation(project(":model-text"))
    implementation(project(":piecetable"))
}
