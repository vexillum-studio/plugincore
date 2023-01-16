package com.vexillum.plugincore.launcher.config

data class PluginCoreConfig(
    val logs: LogConfig
)

data class LogConfig(
    val folder: String,
    val prefixFormat: String,
    val fileFormat: String
)