
plugins {
    id("buildlogic.base")
}

dependencies {
    api(files("../javafx-peep/build/libs/javafx-base.jar"))
    api(files("../javafx-peep/build/libs/javafx-graphics.jar"))
    api(files("../javafx-peep/build/libs/javafx-controls.jar"))
    api(files("../javafx-peep/build/libs/javafx-web.jar"))
    api(files("../javafx-peep/build/libs/javafx-media.jar"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(":javafx-peep:build")
}
