plugins {
 `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}
