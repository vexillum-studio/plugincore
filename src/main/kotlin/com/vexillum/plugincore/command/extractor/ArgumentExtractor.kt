package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.ArgumentExtractException
import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.NoNextArgumentException
import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.Message

/**
 * First step in command argument parse process, parses from [String] to the desired type
 */
interface ArgumentExtractor<Sender : LanguageAgent, Type : Any> {

    val descriptor: (LanguageAgent) -> String

    val extractor: (Sender, String) -> Type

    val errorMessage: ((Sender, value: String) -> String)?
        get() = null

    fun extract(sender: Sender, value: String): Type =
        try {
            extractor(sender, value)
        } catch (e: NoNextArgumentException) {
            throw e
        } catch (e: Exception) {
            errorMessage?.let {
                throw CommandException(it(sender, value))
            } ?: throw ArgumentExtractException(e)
        }

    fun autocomplete(sender: Sender, value: String): List<String> = emptyList()

    fun LanguageAgent.defaultCommandMessage(
        replacements: Map<String, Any> = emptyMap(),
        block: PluginCoreLanguage.() -> Message
    ): String =
        PluginCoreLauncher.instance.resolve(this, replacements, block)
}
