plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    //constraints {
    //    implementation("org.apache.commons:commons-text:1.10.0")
    //}
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}
