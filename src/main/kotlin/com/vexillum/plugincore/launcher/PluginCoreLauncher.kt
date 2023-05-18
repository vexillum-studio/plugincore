package com.vexillum.plugincore.launcher

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.launcher.managers.config.LogConfig
import com.vexillum.plugincore.launcher.managers.config.PluginCoreConfig
import com.vexillum.plugincore.managers.config.ConfigManager

fun main(args: Array<String>) {
    println(args)
}

class PluginCoreLauncher : PluginCore() {

    val configManager = managerFactory.newConfigManager(
        PluginCoreConfig::class,
        "plugincore",
        "config"
    )

    val languageManager = managerFactory.newConfigManager(
        PluginCoreConfig::class,
        "plugincore",
        "config"
    )

    override fun enable() {
        logManager.info("PluginCore started")
    }

    override fun disable() {

    }
}
