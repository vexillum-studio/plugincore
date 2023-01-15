package com.vexillum.plugincore

import com.vexillum.plugincore.manager.log.LogManager
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread

abstract class PluginCore : JavaPlugin() {

    private val logger = LogManager(this)

    final override fun onEnable() {
        thread(start = true, name = name) {
            println("Hello there")
            logger.info("Info")
            logger.warning("Warning")
            logger.error("Error")
            enable()
        }
    }

    final override fun onDisable() {
        disable()
    }

    abstract fun enable()

    abstract fun disable()
}
