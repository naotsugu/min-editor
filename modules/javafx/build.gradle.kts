plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

val idea = System.getenv("XPC_SERVICE_NAME").contains("intellij")

tasks.build {
    if (!idea) {
        dependsOn(gradle.includedBuild("javafx-base").task(":build"))
        dependsOn(gradle.includedBuild("javafx-graphics").task(":build"))
    }
}

dependencies {
    if (!idea) {
        api("com.mammb.code:javafx-base")
        api("com.mammb.code:javafx-graphics")
    } else {
        api(files("../javafx-base/build/libs/javafx-base.jar"))
        api(files("../javafx-graphics/build/libs/javafx-graphics.jar"))
    }
}

group = "com.mammb.code"
