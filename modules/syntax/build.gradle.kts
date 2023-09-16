plugins {
    id("code.editor.base")
}

dependencies {
    api(project(":syntax-base"))
    implementation(project(":syntax-basic"))
    implementation(project(":syntax-java"))
    implementation(project(":syntax-javascript"))
    implementation(project(":syntax-markdown"))
    implementation(project(":syntax-rust"))
    implementation(project(":model-text"))
}
