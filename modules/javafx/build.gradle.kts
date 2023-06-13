
plugins {
    id("code.editor.base")
}

dependencies {
    api(files("../javafx-peep/build/libs/javafx-base.jar"))
    api(files("../javafx-peep/build/libs/javafx-graphics.jar"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(":javafx-peep:build")
}
