package com.vexillum.plugincore.managers.log

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.logger
import org.bukkit.ChatColor
import org.slf4j.Logger

class LogManager internal constructor(
    private val pluginCore: PluginCore,
    private val logger: Logger = pluginCore::class.logger()
) {

    fun debug(vararg message: Any?) =
        log(logger::debug, message)

    fun info(vararg message: Any?) =
        log(logger::info, message)

    fun warning(vararg message: Any?) =
        log(logger::warn, message)

    fun error(vararg message: Any?) =
        log(logger::error, message)

    fun trace(vararg message: Any?) =
        log(logger::trace, message)

    private fun log(logFn: (String) -> Unit, message: Array<out Any?>) {
        val completeMessage = message
            .joinToString(separator = SEPARATOR)
            .let { ChatColor.stripColor(it)!! }
        logFn(completeMessage)
    }

    companion object {
        private const val SEPARATOR = " "
    }
}
