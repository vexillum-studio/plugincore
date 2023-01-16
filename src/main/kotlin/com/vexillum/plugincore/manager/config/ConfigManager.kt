package com.vexillum.plugincore.manager.config

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.util.JsonUtil
import com.vexillum.plugincore.util.JsonUtil.JSON_EXTENSION
import java.io.File
import kotlin.reflect.KClass

/**
 * @param path The config relative path to the plugin's data folder without extension
 */
class ConfigManager<T : Any>(
    private val pluginCore: PluginCore,
    private val configClass: KClass<T>,
    path: String = "config"
) {

    private val pathWithExtension: String = path + JSON_EXTENSION

    var config: T? = null

    fun reload() {
        try {
            pluginCore.logManager.info("Loading ${pluginCore.name} config...")
            config = load()
        } catch (e: Exception) {
            pluginCore.logManager.error("Error loading the config file $e")
        }
    }

    private fun load() : T =
        JsonUtil.mapper.readValue(configFile(), configClass.java)

    fun save(config: T) {
        try {
            val writer = JsonUtil.mapper.writer(DefaultPrettyPrinter())
            writer.writeValue(configFile(), config)
            this.config = config
        } catch (e: Exception) {
            pluginCore.logManager.error("Error saving the config file $e")
        }
    }

    private fun configFile() =
        File(pluginCore.dataFolder, pathWithExtension)

}
