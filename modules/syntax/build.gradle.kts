plugins {
    id("code.editor.base")
}

dependencies {
    api(project(":syntax-base"))
    implementation(project(":syntax-java"))
    implementation(project(":model-text"))
}
