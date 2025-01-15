package com.vexillum.plugincore.managers.config

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.copyTo
import com.vexillum.plugincore.extensions.loadResourceAsStream
import com.vexillum.plugincore.util.JsonUtil
import com.vexillum.plugincore.util.JsonUtil.JSON_EXTENSION
import java.io.File
import kotlin.reflect.KClass

class ConfigManager<T : Any> internal constructor(
    pluginCore: PluginCore,
    private val configName: String,
    private val configClass: KClass<T>,
    private val originFolderPath: String,
    private val destinationFolderPath: String
) : PluginCore by pluginCore {

    private val destinationFile by lazy {
        File(plugin.dataFolder, "/$destinationFolderPath/$configName$JSON_EXTENSION")
    }

    private lateinit var config: T

    fun save(config: T) {
        try {
            val writer = JsonUtil.mapper.writer(DefaultPrettyPrinter())
            writer.writeValue(destinationFile, config)
            this.config = config
        } catch (e: Exception) {
            logManager.error("Error saving the config file $e")
        }
    }

    fun reload() {
        val fileName = "$configName$JSON_EXTENSION"
        try {
            logManager.info("Loading $fileName config file...")
            config = load()
            logManager.info("Config file $fileName loaded successfully")
        } catch (e: Exception) {
            logManager.error("Error loading the config file $fileName: $e")
        }
    }

    private fun load(): T {
        try {
            val destinationFolder = destinationFile.parentFile
            if (!destinationFolder.exists() && !destinationFolder.mkdirs()) {
                logManager.error("Can't create the config container folder")
            }
            if (!destinationFile.exists()) {
                copyFromOrigin()
            }
        } catch (e: Exception) {
            val message = when (e) {
                is IllegalStateException -> e.message
                else -> "IO exception copying the origin configuration to the plugin's data folder file: $destinationFolderPath"
            }
            logManager.error(message)
            throw e
        }
        return JsonUtil.mapper.readValue(destinationFile, configClass.java)
    }

    private fun copyFromOrigin() {
        val originPath = "$originFolderPath/$configName$JSON_EXTENSION"
        logManager.info("Copying origin configuration from: $originPath")
        val inputStream = this::class.loadResourceAsStream(originPath)
            ?: error("Can't find the origin configuration file from: $originPath")
        inputStream.use {
            destinationFile.createNewFile()
            it.copyTo(destinationFile)
        }
    }

    operator fun invoke(): T {
        if (!::config.isInitialized) {
            config = load()
        }
        return config
    }
}
