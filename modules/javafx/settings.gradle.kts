plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "javafx"

val idea = System.getenv("XPC_SERVICE_NAME").contains("intellij")
if (!idea) {
    includeBuild("../javafx-base")
    includeBuild("../javafx-graphics")
}
