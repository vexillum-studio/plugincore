package com.vexillum.plugincore.launcher

import com.vexillum.plugincore.PluginCoreBase
import com.vexillum.plugincore.PluginCoreBoot
import com.vexillum.plugincore.extensions.disablePlugin
import com.vexillum.plugincore.launcher.command.registerCommands
import com.vexillum.plugincore.launcher.managers.config.PluginCoreConfig
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.launcher.player.PluginCorePlayerManager
import com.vexillum.plugincore.managers.language.DefaultLanguageResolver

fun main(args: Array<String>) {
    println(args)
}

class PluginCoreLauncher internal constructor() : PluginCoreBase(), DefaultLanguageResolver {

    init {
        pluginCoreInstance = this
    }

    override val defaultLanguageContext = managerFactory.newLanguageManager(
        PluginCoreLanguage::class,
        "language",
        "language"
    )

    val configManager = managerFactory.newConfigManager(
        PluginCoreConfig::class,
        "plugincore",
        "config"
    )

    internal val playerManager: PluginCorePlayerManager = PluginCorePlayerManager()

    override fun enable() {
        logManager.info("PluginCore started")
        registerCommands(this)
    }

    override fun afterEnable() {
        if (defaultLanguageContext.loadedLanguages.isEmpty()) {
            logManager.error("PluginCore has been disabled, wasn't able to load the default languages")
            plugin.disablePlugin()
        }
    }

    override fun disable() {
    }

    companion object {
        internal lateinit var pluginCoreInstance: PluginCoreLauncher
    }
}

class Launcher : PluginCoreBoot(PluginCoreLauncher())
