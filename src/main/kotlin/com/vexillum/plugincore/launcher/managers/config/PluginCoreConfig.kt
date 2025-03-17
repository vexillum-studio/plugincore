package com.vexillum.plugincore.launcher.managers.config

import java.time.format.DateTimeFormatter

data class PluginCoreConfig(
    val monospacedFont: Boolean,
    val dateformat: DateTimeFormatter,
    val humanDateFormat: DateTimeFormatter,
    val timeFormat: DateTimeFormatter,
    val logs: LogConfig
)

data class LogConfig(
    val folder: String,
    val prefixFormat: String,
    val fileFormat: String
)
