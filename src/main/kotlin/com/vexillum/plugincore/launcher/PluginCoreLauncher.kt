package com.vexillum.plugincore.launcher

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.launcher.config.PluginCoreConfig
import com.vexillum.plugincore.manager.config.ConfigManager

fun main(args: Array<String>) {
    println(args)
}

class PluginCoreLauncher : PluginCore() {

    val configManager = ConfigManager(
        this,
        PluginCoreConfig::class,
        "/config/plugincore"
    )

    override fun enable() {
        logManager.info("PluginCore started")
        configManager.reload()
        logManager.info(configManager.config)
    }

    override fun disable() {

    }
}
