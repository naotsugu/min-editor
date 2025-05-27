plugins {
    `java-library`
    id("org.openrewrite.rewrite")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
        vendor = JvmVendorSpec.AMAZON // see https://github.com/beryx/badass-jlink-plugin/issues/299
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

tasks.withType<Javadoc>().configureEach {
    options {
        this as StandardJavadocDocletOptions
        addBooleanOption("-enable-preview", true)
        addStringOption("-release", "24")
    }
}

rewrite {
    isExportDatatables = true
    configFile = project.rootProject.file("rewrite.yml")
    exclusion(
        "modules/piecetable/*",
    )
    activeRecipe("com.mammb.Format")
    activeStyle("com.mammb.Style")
}
