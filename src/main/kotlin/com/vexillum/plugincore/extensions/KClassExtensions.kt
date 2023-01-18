package com.vexillum.plugincore.extensions

import java.io.File
import java.io.InputStream
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun KClass<*>.logger() =
    LoggerFactory.getLogger(this.java)

fun KClass<*>.loadResource(path: String): File? =
    java.getResource(path)?.file?.let { File(it) }

fun KClass<*>.loadResourceAsString(path: String): String? =
    loadResourceAsStream(path)?.readBytes()?.toString(Charsets.UTF_8)

fun KClass<*>.loadResourceAsStream(path: String): InputStream? =
    java.classLoader.getResourceAsStream(path)
