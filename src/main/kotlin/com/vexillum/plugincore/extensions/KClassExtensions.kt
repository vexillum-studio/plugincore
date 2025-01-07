package com.vexillum.plugincore.extensions

import com.vexillum.plugincore.util.copyFilesFromPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
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
    loadResourceAsStream(path)?.use { inputStream ->
        inputStream.readBytes().toString(Charsets.UTF_8)
    }

/**
 * Loads a resource as a file from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResourceAsFile(path: String): File? =
    loadResourceAsURL(path)?.file?.let { File(it) }

/**
 * If the path points to a jar, loads a resource from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun <R> KClass<*>.loadResource(
    path: String,
    block: (Path) -> R
): R {
    val uri = loadResourceAsURL(path)?.toURI() ?: error("No resource found at: $path")
    return if (uri.scheme == "jar") {
        val fileSystem = try {
            FileSystems.getFileSystem(uri)
        } catch (e: FileSystemNotFoundException) {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>())
        }
        fileSystem.use {
            block(fileSystem.getPath(path))
        }
    } else {
        block(Paths.get(uri))
    }
}

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
    loadResource(resourcePath) { originPath ->
        copyFilesFromPath(originPath, destination)
    }
}
