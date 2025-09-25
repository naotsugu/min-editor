plugins {
    `java-library`
    id("org.openrewrite.rewrite")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.ADOPTIUM
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
        addStringOption("-release", "25")
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
