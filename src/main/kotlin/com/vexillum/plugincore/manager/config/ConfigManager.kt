package com.vexillum.plugincore.manager.config

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.copyTo
import com.vexillum.plugincore.extensions.loadResourceAsStream
import com.vexillum.plugincore.util.JsonUtil
import com.vexillum.plugincore.util.JsonUtil.JSON_EXTENSION
import java.io.File
import kotlin.reflect.KClass

class ConfigManager<T : Any> internal constructor(
    val name: String,
    private val pluginCore: PluginCore,
    private val configClass: KClass<T>,
    private val originPath: String,
    private val destinationPath: String
) {

    private val destinationFile = File(pluginCore.dataFolder, "/$destinationPath/$name$JSON_EXTENSION")
    private var config: T = load()

    fun save(config: T) {
        try {
            val writer = JsonUtil.mapper.writer(DefaultPrettyPrinter())
            writer.writeValue(destinationFile, config)
            this.config = config
        } catch (e: Exception) {
            pluginCore.logManager.error("Error saving the config file $e")
        }
    }

    fun reload() {
        try {
            pluginCore.logManager.info("Loading $name$JSON_EXTENSION config...")
            config = load()
        } catch (e: Exception) {
            pluginCore.logManager.error("Error loading the config file $e")
        }
    }

    private fun load() : T {
        try {
            val destinationFolder = destinationFile.parentFile
            if (!destinationFolder.exists() && !destinationFolder.mkdirs()) {
                pluginCore.logManager.error("Can't create the config container folder")
            }
            if(!destinationFile.exists()) {
                copyFromOrigin()
            }
        } catch (e: Exception) {
            val message = when(e) {
                is IllegalStateException -> e.message
                else -> "IO exception copying the origin configuration to the plugin's data folder file: $destinationPath"
            }
            pluginCore.logManager.error(message)
            throw e
        }
        return JsonUtil.mapper.readValue(destinationFile, configClass.java)
    }

    private fun copyFromOrigin() {
        val originPath = "$originPath/$name$JSON_EXTENSION"
        pluginCore.logManager.info("Copying origin configuration from: $originPath")
        val inputStream = this::class.loadResourceAsStream(originPath) ?:
        error("Can't find the origin configuration file from: $originPath")
        destinationFile.createNewFile()
        inputStream.copyTo(destinationFile)
    }

    operator fun invoke() = config

}
