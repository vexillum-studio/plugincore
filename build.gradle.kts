import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "co.vexillum"
version = "1.0-SNAPSHOT"

val jarName = "KotlinTest"
val spigotVersion = "1.19.2-R0.1-SNAPSHOT"
val mainClass = "com.vexillum.test.KotlinTest"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        mavenContent {
            releasesOnly()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.spigotmc:spigot-api:$spigotVersion")
    testImplementation(kotlin("test"))
}

tasks.withType<Jar> {
    archiveFileName.set("$jarName.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = mainClass
    }

    from(sourceSets.main.get().output)

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
    kotlinOptions.jvmTarget = "16"
}