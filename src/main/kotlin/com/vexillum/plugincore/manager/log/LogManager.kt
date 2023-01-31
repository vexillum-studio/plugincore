package com.vexillum.plugincore.manager.log

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.logger
import org.slf4j.Logger

class LogManager internal constructor(
    private val pluginCore: PluginCore
) {
    private val logger: Logger = pluginCore::class.logger()

    fun debug(message: Any?) =
        log(message, logger::debug)

    fun info(message: Any?) =
        log(message, logger::info)

    fun warning(message: Any?) =
        log(message, logger::warn)

    fun error(message: Any?) =
        log(message, logger::error)

    fun trace(message: Any?) =
        log(message, logger::trace)

    private fun log(message: Any?, logFn: (String) -> Unit) {
        logFn(message.toString())
    }

}
