package com.vexillum.plugincore

import com.vexillum.plugincore.extensions.loadResourceAsString
import com.vexillum.plugincore.extensions.registerEvents
import com.vexillum.plugincore.extensions.runOnNextTick
import com.vexillum.plugincore.managers.ManagerFactory
import com.vexillum.plugincore.managers.command.CommandManager
import com.vexillum.plugincore.managers.log.LogManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

interface PluginCore : Plugin, Listener {
    val managerFactory: ManagerFactory
    val logManager: LogManager
    val commandManager: CommandManager
}

abstract class PluginCoreBase : JavaPlugin(), PluginCore {

    final override val managerFactory: ManagerFactory = ManagerFactory(this)
    override val logManager: LogManager = managerFactory.newLogManager()
    override val commandManager: CommandManager = managerFactory.commandManager()

    final override fun onEnable() {
        this::class.loadResourceAsString(DEFAULT_BANNER_PATH)?.let {
            println(it)
        }
        registerEvents(this)
        managerFactory.start()
        enable()
    }

    final override fun onDisable() {
        disable()
        managerFactory.stop()
    }

    abstract fun enable()

    abstract fun disable()

    open fun afterEnable() {}

    @EventHandler
    fun on(event: PluginEnableEvent) {
        if (event.plugin === this) {
            runOnNextTick(this::afterEnable)
        }
    }

    companion object {
        private const val DEFAULT_BANNER_PATH = "banner.txt"
    }
}
