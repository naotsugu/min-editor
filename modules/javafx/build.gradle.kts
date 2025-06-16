
plugins {
    id("buildlogic.base")
}

dependencies {
    api(files("../javafx-peep/build/libs/javafx-base.jar"))
    api(files("../javafx-peep/build/libs/javafx-graphics.jar"))
    api(files("../javafx-peep/build/libs/javafx-controls.jar"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(":javafx-peep:build")
}
