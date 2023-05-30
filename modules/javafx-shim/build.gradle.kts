plugins {
    id("code.editor.base")
}

dependencies {
    implementation("com.mammb.code:javafx")
    api(project(":model-layout"))
}
