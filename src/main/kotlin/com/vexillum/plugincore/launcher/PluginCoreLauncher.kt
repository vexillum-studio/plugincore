package com.vexillum.plugincore.launcher

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.launcher.config.LogConfig
import com.vexillum.plugincore.launcher.config.PluginCoreConfig
import com.vexillum.plugincore.manager.config.ConfigManager

fun main(args: Array<String>) {
    println(args)
}

class PluginCoreLauncher : PluginCore() {

    val configManager = managerFactory.newConfigManager(
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
