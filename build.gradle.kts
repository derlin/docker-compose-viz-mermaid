import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "ch.derlin"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:1.29")
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("com.microsoft.playwright:playwright:1.17.1")
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.25")
}

tasks.jar {
    val noPlaywright = project.hasProperty("noPlaywright")
    if(noPlaywright) {
        archiveBaseName.set("${archiveBaseName.get()}_no_local")
    }
    manifest {
        attributes += "main-Class" to "ch.derlin.dcvizmermaid.CliKt"
        if (noPlaywright) {
            // set default renderer to mermaid.ink if ran with the argument -PnoPlaywright
            attributes += "env" to mapOf("MERMAID_RENDERER" to "mermaid.ink")
        }
    }
    from(configurations.runtimeClasspath.get()
        // exclude playwright dependencies if ran with the argument -PnoPlaywright
        .filter { !(noPlaywright && it.path.contains("/com.microsoft.playwright/")) }
        .map { if (it.isDirectory) it else zipTree(it) })
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false } // always run tests !
    testLogging {
        // get actual information about failed tests in the console
        // should be used inside
        showStackTraces = true
        showCauses = true
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
    }
    filter {
        // use ./gradlew test -Pgenerate to generate also the docs
        if (project.hasProperty("docs")) {
            environment(mapOf("GEN_DOCS" to "1"))
            //excludeTestsMatching("*Generator*")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}