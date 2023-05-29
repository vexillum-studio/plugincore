package com.vexillum.plugincore.managers

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.managers.command.CommandManager
import com.vexillum.plugincore.managers.config.ConfigManager
import com.vexillum.plugincore.managers.language.LanguageManager
import com.vexillum.plugincore.managers.log.LogManager
import kotlin.reflect.KClass

class ManagerFactory(
    private val pluginCore: PluginCore
) {

    private val configManagers = mutableMapOf<KClass<*>, ConfigManager<*>>()
    private val languageManagers = mutableMapOf<KClass<*>, LanguageManager<*>>()

    internal fun start() {
        configManagers.values.forEach { it.reload() }
        languageManagers.values.forEach { it.reload() }
    }

    private val commandManager by lazy {
        CommandManager(pluginCore)
    }

    fun newLogManager() =
        LogManager(pluginCore)

    fun <T : Any> newConfigManager(
        configClass: KClass<T>,
        name: String = pluginCore.name,
        originPath: String = "",
        destinationPath: String = ""
    ) =
        ConfigManager(
            pluginCore,
            name,
            configClass,
            originFolderPath = originPath,
            destinationFolderPath = destinationPath
        ).also { configManagers[configClass] = it }

    fun <T : Any> newLanguageManager(
        languageClass: KClass<T>,
        originFolderPath: String = "",
        destinationFolderPath: String = ""
    ) =
        LanguageManager(
            pluginCore,
            languageClass,
            originFolderPath = originFolderPath,
            destinationFolderPath = destinationFolderPath
        ).also { languageManagers[languageClass] = it }

    fun commandManager() =
        commandManager
}
