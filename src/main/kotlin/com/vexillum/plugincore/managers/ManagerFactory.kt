package com.vexillum.plugincore.managers

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.managers.command.CommandManager
import com.vexillum.plugincore.managers.config.ConfigManager
import com.vexillum.plugincore.managers.language.LanguageManager
import com.vexillum.plugincore.managers.log.LogManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ManagerFactory(
    val pluginCore: PluginCore
) {

    private val started = AtomicBoolean(false)
    private val configManagers = mutableMapOf<KClass<*>, ConfigManager<*>>()
    private val languageManagers = mutableMapOf<KClass<*>, LanguageManager<*>>()
    private val commandManager by lazy {
        CommandManager(pluginCore)
    }

    internal fun start() {
        configManagers.values.forEach { it.reload() }
        languageManagers.values.forEach { it.reload() }
        started.set(true)
    }

    internal fun stop() {
        configManagers.values.forEach { it.plugin.saveConfig() }
        commandManager.unregisterAll()
        started.set(false)
    }

    fun reload() {
        stop()
        start()
    }

    fun newLogManager() =
        LogManager(pluginCore)

    fun <T : Any> newConfigManager(
        configClass: KClass<T>,
        name: String = pluginCore.plugin.name,
        originPath: String = "",
        destinationPath: String = ""
    ) =
        ConfigManager(
            pluginCore,
            name,
            configClass,
            originFolderPath = originPath,
            destinationFolderPath = destinationPath
        ).also {
            configManagers[configClass] = it
            if (started.get()) {
                it.reload()
            }
        }

    fun <T : Language> newLanguageManager(
        languageClass: KClass<T>,
        originFolderPath: String = "",
        destinationFolderPath: String = ""
    ) =
        LanguageManager(
            pluginCore,
            languageClass,
            originFolderPath = originFolderPath,
            destinationFolderPath = destinationFolderPath
        ).also {
            languageManagers[languageClass] = it
            if (started.get()) {
                it.reload()
            }
        }

    fun <T : Any> config(configClass: KClass<T>): ConfigManager<T> =
        configManagers[configClass] as? ConfigManager<T>
            ?: error("No ConfigManager registered for $configClass")

    inline fun <reified T : Any> config() =
        config(T::class)

    fun <T : Language> language(languageClass: KClass<T>): LanguageManager<T> =
        languageManagers[languageClass] as? LanguageManager<T>
            ?: error("No LanguageManager registered for $languageClass")

    inline fun <reified T : Language> language() =
        language(T::class)

    fun commandManager() =
        commandManager
}
