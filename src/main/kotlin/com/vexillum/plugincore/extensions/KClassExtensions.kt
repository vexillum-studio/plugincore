package com.vexillum.plugincore.extensions

import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.reflect.KClass

fun KClass<*>.logger() =
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
    loadResourceAsURL(path)?.let { Path.of(it.toURI()) }

/**
 * Loads a resource as a file from a path relative to the jar's resources structure without preceding '/'
 * Example: data/config.json
 */
fun KClass<*>.loadResourceAsURL(path: String): URL? =
    java.getResource("/$path")
