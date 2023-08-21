plugins {
    id("code.editor.base")
}

dependencies {
    api(project(":syntax-base"))
    implementation(project(":syntax-java"))
    implementation(project(":syntax-markdown"))
    implementation(project(":model-text"))
}
