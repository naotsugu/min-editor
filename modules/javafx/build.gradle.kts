plugins {
    id("code.editor.base")
    java
    id("org.openjfx.javafxplugin") version "0.0.14"
}

javafx {
    version = "20"
    modules("javafx.graphics")
}


val exportsOpt = arrayOf(
    "--add-exports", "javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
    "--add-exports", "javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED")

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(exportsOpt)
}

tasks.withType<Test>().configureEach {
    jvmArgs(*exportsOpt)
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs(*exportsOpt)
}

tasks.withType<Javadoc>().configureEach {
    options {
        this as StandardJavadocDocletOptions
        addMultilineStringsOption("-add-exports").setValue(
            exportsOpt.slice(1..exportsOpt.size step 2))
    }
}

tasks.jar {
    manifest {
        attributes("Add-Exports" to "javafx.graphics/com.sun.javafx.tk javafx.graphics/com.sun.javafx.scene.text")
        attributes("Automatic-Module-Name" to "code.editor.javafx")
    }
}

