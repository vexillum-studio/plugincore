package com.vexillum.plugincore.manager.log

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.logger
import org.slf4j.Logger

class LogManager(
    val pluginCore: PluginCore
) {
    private val logger: Logger = logger()

    fun debug(message: String) =
        log(message, logger::debug)

    fun info(message: String) =
        log(message, logger::info)

    fun warning(message: String) =
        log(message, logger::warn)

    fun error(message: String) =
        log(message, logger::error)

    private fun log(message: String, loggerFn: (String) -> Unit) {
        loggerFn(message)
    }
}
