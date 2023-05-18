package com.vexillum.plugincore.managers

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.managers.config.ConfigManager
import com.vexillum.plugincore.managers.language.LanguageManager
import com.vexillum.plugincore.managers.log.LogManager
import kotlin.reflect.KClass

class ManagerFactory(
    private val pluginCore: PluginCore
) {

    fun newLogManager() =
        LogManager(pluginCore)

    fun <T : Any> newConfigManager(
        configClass: KClass<T>,
        name: String = pluginCore.name,
        originPath: String = "",
        destinationPath: String = ""
    ) =
        ConfigManager(
            name,
            pluginCore,
            configClass,
            originFolderPath = originPath,
            destinationFolderPath = destinationPath
        )

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
        )
}
