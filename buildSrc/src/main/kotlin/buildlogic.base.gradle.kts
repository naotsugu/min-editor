plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    //constraints {
    //    implementation("org.apache.commons:commons-text:1.10.0")
    //}
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

// enable-preview
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test>().configureEach {
    jvmArgs("--enable-preview")
}

//tasks.withType<JavaExec>().configureEach {
//    jvmArgs("--enable-preview")
//}

tasks.withType<Javadoc>().configureEach {
    options {
        this as StandardJavadocDocletOptions
        addBooleanOption("-enable-preview", true)
        addStringOption("-release", "21")
    }
}
