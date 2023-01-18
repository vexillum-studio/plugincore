package com.vexillum.plugincore.manager

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.manager.config.ConfigManager
import com.vexillum.plugincore.manager.log.LogManager
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
            originPath = originPath,
            destinationPath = destinationPath
        )

}