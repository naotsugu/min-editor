plugins {
    id("buildlogic.base")
}

dependencies {
    implementation(project(":core"))
    api(project(":ui-base"))
    implementation(project(":javafx"))
}
