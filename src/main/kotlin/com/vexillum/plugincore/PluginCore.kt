package com.vexillum.plugincore

import com.vexillum.plugincore.manager.log.LogManager
import org.bukkit.plugin.java.JavaPlugin

abstract class PluginCore : JavaPlugin() {

    val logManager = LogManager(this)

    final override fun onEnable() {
        enable()
    }

    final override fun onDisable() {
        disable()
    }

    abstract fun enable()

    abstract fun disable()
}
