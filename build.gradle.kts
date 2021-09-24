import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
}

group = "ch.derlin"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:1.29")
    implementation("com.github.ajalt:clikt:2.8.0")
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.24")
}

tasks.jar {
    manifest {
        attributes += "main-Class" to "ch.derlin.dcvizmermaid.CliKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
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