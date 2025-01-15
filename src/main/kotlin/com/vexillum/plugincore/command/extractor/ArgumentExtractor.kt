package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.ArgumentExtractException
import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.NoNextArgumentException
import com.vexillum.plugincore.command.suggestion.CommandSuggestion
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.context.LanguageAgentContext

/**
 * First step in command argument parse process, parses from [String] to the desired type
 */
@Suppress("RethrowCaughtException")
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

    fun autocomplete(sender: Sender, value: String): List<CommandSuggestion<Sender>> = emptyList()

    fun describe(context: LanguageAgentContext<PluginCoreLanguage>): String =
        with(context) {
            val color = resolve { command.descriptor.color }
            val prefix = resolve { command.descriptor.prefix }
            val postfix = resolve { command.descriptor.postfix }
            "$color$prefix${descriptor(this)}$postfix"
        }
}
