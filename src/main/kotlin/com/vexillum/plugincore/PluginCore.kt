package com.vexillum.plugincore

import com.vexillum.plugincore.extensions.loadResourceAsString
import com.vexillum.plugincore.manager.ManagerFactory
import com.vexillum.plugincore.manager.log.LogManager
import org.bukkit.plugin.java.JavaPlugin

abstract class PluginCore : JavaPlugin() {

    val managerFactory = ManagerFactory(this)
    val logManager = managerFactory.newLogManager()

    final override fun onEnable() {
        this::class.loadResourceAsString(DEFAULT_BANNER_PATH)?.let {
            logManager.trace("\n$it\n")
        }
        enable()
    }

    final override fun onDisable() {
        disable()
    }

    abstract fun enable()

    abstract fun disable()

    companion object {
        private const val DEFAULT_BANNER_PATH = "/banner.txt"
    }

}
