plugins {
    `java-library`
    id("code.editor.base")
}

dependencies {
    implementation(project(":javafx"))
    implementation(project(":model-text"))
    implementation(project(":model-layout"))
}

