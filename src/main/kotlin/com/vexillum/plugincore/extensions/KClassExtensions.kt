package com.vexillum.plugincore.extensions

import java.io.File
import java.io.InputStream
import org.slf4j.LoggerFactory
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
    java.getResource("/$path")?.file?.let { File(it) }