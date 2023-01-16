package com.vexillum.plugincore.extensions

import java.io.File
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun KClass<*>.logger() =
    LoggerFactory.getLogger(this.java)

fun KClass<*>.loadResource(path: String): File? =
    java.classLoader.getResource(path)?.file?.let { File(it) }
