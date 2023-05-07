plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":piecetable"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

application {
    mainClass.set("com.mammb.code.editor.App")
    mainModule.set("com.mammb.code.editor")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

javafx {
    version = "20"
    modules("javafx.controls")
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}
