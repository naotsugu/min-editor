plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "javafx"

val idea = System.getProperty("idea.active") == "true" ||
           System.getProperty("idea.sync.active") == "true"

if (!idea) {
    includeBuild("../javafx-base")
    includeBuild("../javafx-graphics")
}
