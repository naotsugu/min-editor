plugins {
    id("code.editor.base")
    application
}

dependencies {
    implementation(project(":ui-app"))
}

application {
    mainClass.set("com.mammb.code.editor.bootstrap.Main")
    mainModule.set("code.editor.bootstrap")
}
