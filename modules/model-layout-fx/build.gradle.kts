plugins {
    `java-library`
    id("buildlogic.base")
}

dependencies {
    implementation(project(":javafx"))
    implementation(project(":model-text"))
    implementation(project(":model-layout"))
}

