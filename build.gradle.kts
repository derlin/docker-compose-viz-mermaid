import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.gorylenko.gradle-git-properties") version "2.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("io.gitlab.arturbosch.detekt").version("1.20.0-RC2")
}

group = "ch.derlin"
// Get the version from version.txt. It should NOT contain -SNAPSHOT, but be THE LATEST RELEASED version.
// The version will be computed automatically as <next-patch-version>-SNAPSHOT.
// In case `-Dsnapshot=false` is passed to the gradle command, the version will be used as is (useful during release)
version = file("version.txt").readText().let {
    val match = requireNotNull("(\\d+\\.\\d+\\.)(\\d+)".toRegex().find(it)) { "Could not extract version from version.txt" }
    if (System.getProperty("snapshot") in listOf("0", "false")) match.value
    else match.groupValues[1] + (match.groupValues[2].toInt() + 1) + "-SNAPSHOT"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.github.ajalt:clikt:2.8.0")
    implementation("com.microsoft.playwright:playwright:1.31.0")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.25")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val noPlaywright = project.hasProperty("noPlaywright")
    if (noPlaywright) {
        archiveBaseName.set("${archiveBaseName.get()}_no_local")
    }
    manifest {
        attributes += "main-Class" to "ch.derlin.dcvizmermaid.MainKt"
        if (noPlaywright) {
            // set default renderer to mermaid.ink if ran with the argument -PnoPlaywright
            attributes += "env" to mapOf("MERMAID_RENDERER" to "mermaid.ink")
        }
    }
    from(
        configurations.runtimeClasspath.get()
            // exclude playwright dependencies if ran with the argument -PnoPlaywright
            .filter { !(noPlaywright && it.path.contains("/com.microsoft.playwright/")) }
            .map { if (it.isDirectory) it else zipTree(it) }
    )
}

abstract class ExecutableJarTask : DefaultTask() {
    // This custom task will prepend the content of a bash launch script
    // at the beginning of a jar, and make it executable (chmod +x)

    @InputFiles
    var originalJars: ConfigurableFileTree = project.fileTree("${project.buildDir}/libs") { include("*.jar") }

    @OutputDirectory
    var outputDir: File = project.buildDir.resolve("bin") // where to write the modified jar(s)

    @InputFile
    var launchScript: File = project.rootDir.resolve("launch.sh") // script to prepend

    @TaskAction
    fun createExecutableJars() {
        project.mkdir(outputDir)
        originalJars.forEach { jar ->
            outputDir.resolve(jar.name).run {
                outputStream().use { out ->
                    out.write(launchScript.readBytes())
                    out.write(jar.readBytes())
                }
                setExecutable(true)
                println("created executable: $path")
            }
        }
    }
}

tasks.register<ExecutableJarTask>("exec-jar") {
    // dependsOn("jar") // since we have two flavors (-PnoPlaywright), don't depend on jar
    launchScript = project.rootDir.resolve("bin/launcher.sh")
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
        // use ./gradlew test -Pdocs to generate also the docs
        if (project.hasProperty("docs")) {
            environment(mapOf("GEN_DOCS" to "1"))
            // excludeTestsMatching("*Generator*")
        }
    }
}

gitProperties {
    gitPropertiesName = "info.properties"
    keys = listOf("git.build.version", "git.branch", "git.commit.id", "git.commit.message.short", "git.commit.time", "git.dirty")
}

detekt {
    config = files(".detekt.yaml")
    buildUponDefaultConfig = true
}
