plugins {
    id("code.editor.base")
}

dependencies {
    implementation(project(":model-text"))
    implementation(project(":model-buffer"))
    implementation(project(":model-edit"))
}
