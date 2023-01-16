import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.vexillum"
version = "1.0-SNAPSHOT"

val jarName = "PluginCore"
val spigotVersion = "1.19.3-R0.1-SNAPSHOT"
val mainClass = "com.vexillum.plugincore.launcher.PluginCoreLauncher"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        mavenContent {
            releasesOnly()
        }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    // Spigot
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")
    // Sl4j
    implementation("org.slf4j:slf4j-api:2.0.5")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // Mordant
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta9")
    // Testing
    testImplementation("org.spigotmc:spigot-api:$spigotVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.jar {
    archiveFileName.set("$jarName.jar")

    manifest {
        attributes["Implementation-Title"] = jarName
        attributes["Main-Class"] = mainClass
        attributes["Implementation-Version"] = archiveVersion
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
