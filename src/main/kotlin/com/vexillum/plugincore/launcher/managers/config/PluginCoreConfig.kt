package com.vexillum.plugincore.launcher.managers.config

data class PluginCoreConfig(
    val monospacedFont: Boolean,
    val logs: LogConfig
)

data class LogConfig(
    val folder: String,
    val prefixFormat: String,
    val fileFormat: String
)
