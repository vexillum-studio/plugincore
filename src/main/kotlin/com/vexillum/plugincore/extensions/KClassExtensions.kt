package com.vexillum.plugincore.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlin.io.path.toPath
import kotlin.reflect.KClass

fun KClass<*>.logger(): Logger =
    LoggerFactory.getLogger(this.java)

/**
 * Loads a resource as a stream from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResourceAsStream(path: String): InputStream? =
    java.classLoader.getResourceAsStream(path)

/**
 * Loads a resource as a string from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResourceAsString(path: String): String? =
    loadResourceAsStream(path)?.readBytes()?.toString(Charsets.UTF_8)

/**
 * Loads a resource as a file from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResourceAsFile(path: String): File? =
    loadResourceAsURL(path)?.file?.let { File(it) }

/**
 * Loads a resource from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResource(path: String): Path? =
    loadResourceAsURL(path)?.toURI()?.toPath()

/**
 * Loads a resource as a file from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResourceAsURL(path: String): URL? =
    java.getResource("/$path")

fun KClass<*>.jarURL(): URL =
    java.protectionDomain
        .codeSource
        .location

fun KClass<*>.copyResourceTo(resourcePath: String, destination: Path) {
    val jarUrl = jarURL()
    val classLoader = URLClassLoader(arrayOf(jarUrl))
    val fileSystem = FileSystems.newFileSystem(Path.of(jarUrl.toURI()), classLoader)
    val sourcePath = fileSystem.getPath(resourcePath)

    Files.walk(sourcePath).use { paths ->
        paths
            .filter { Files.isRegularFile(it) }
            .forEach { path ->
                val relativeToSource = sourcePath.relativize(path)
                val destinationPath = destination.resolve(relativeToSource.toString())
                Files.copy(path, destinationPath, REPLACE_EXISTING)
            }
    }
}
