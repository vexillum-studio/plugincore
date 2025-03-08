package com.vexillum.plugincore

import com.vexillum.plugincore.extensions.loadResourceAsString
import com.vexillum.plugincore.extensions.registerEvents
import com.vexillum.plugincore.extensions.runOnNextTick
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.context.LanguageContext
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.ManagerFactory
import com.vexillum.plugincore.managers.command.CommandManager
import com.vexillum.plugincore.managers.language.DefaultLanguageResolver
import com.vexillum.plugincore.managers.language.DefaultLanguageResolverImpl
import com.vexillum.plugincore.managers.log.LogManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.java.JavaPlugin

interface PluginCore : LanguageContext<PluginCoreLanguage> {
    val plugin: JavaPlugin
    val managerFactory: ManagerFactory
    val logManager: LogManager
    val commandManager: CommandManager
    fun enable()
    fun disable()
    fun afterEnable()
}

abstract class PluginCoreBase : PluginCore, DefaultLanguageResolver, Listener {

    final override val pluginCore = this

    override lateinit var plugin: JavaPlugin
        internal set
    final override val managerFactory: ManagerFactory by lazy {
        ManagerFactory(this)
    }
    final override val logManager: LogManager by lazy {
        managerFactory.newLogManager()
    }
    final override val commandManager: CommandManager by lazy {
        managerFactory.commandManager()
    }

    override val defaultLanguageContext: LanguageContext<PluginCoreLanguage> by
    DefaultLanguageResolverImpl(managerFactory)

    final override fun language(localLanguage: LocalLanguage): Language<PluginCoreLanguage> =
        defaultLanguageContext.language(localLanguage)
}

abstract class PluginCoreBoot(
    val pluginCore: PluginCoreBase
) : JavaPlugin() {

    final override fun onEnable() {
        pluginCore.plugin = this
        this::class.loadResourceAsString(DEFAULT_BANNER_PATH)?.let {
            println(it)
        }
        registerEvents(pluginCore)
        pluginCore.managerFactory.start()
        pluginCore.enable()
    }

    final override fun onDisable() {
        pluginCore.disable()
        pluginCore.managerFactory.stop()
    }

    @EventHandler
    fun on(event: PluginEnableEvent) {
        if (event.plugin === this) {
            runOnNextTick(pluginCore::afterEnable)
        }
    }

    companion object {
        private const val DEFAULT_BANNER_PATH = "banner.txt"
    }
}
