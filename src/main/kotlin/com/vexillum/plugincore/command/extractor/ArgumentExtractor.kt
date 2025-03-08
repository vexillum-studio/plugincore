package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.ArgumentExtractException
import com.vexillum.plugincore.command.NoNextArgumentException
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.CommandSuggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

/**
 * First step in command argument parse process, parses from [String] to the desired type
 */
@Suppress("RethrowCaughtException")
interface ArgumentExtractor<Sender : LanguageAgent, Type : Any> {

    val extractor: (CommandUser<Sender>, String) -> Type

    val descriptor: (CommandUser<*>) -> LanguageMessage

    val errorMessage: ((CommandUser<*>, value: String) -> LanguageMessage)?
        get() = null

    fun descriptor(user: CommandUser<*>): LanguageMessage

    fun errorMessage(user: CommandUser<*>, value: String): LanguageMessage

    fun extract(user: CommandUser<Sender>, value: String): Type =
        try {
            extractor(user, value)
        } catch (e: NoNextArgumentException) {
            throw e
        } catch (e: Exception) {
            throw ArgumentExtractException(
                message = errorMessage(user, value),
                descriptor = descriptor(user)
            )
        }

    fun autocomplete(sender: Sender, value: String): List<CommandSuggestion<Sender>> =
        emptyList()

    fun describe(user: CommandUser<*>): LanguageMessage =
        with(user) {
            val prefix = resolve { command.descriptor.prefix }
            val color = resolve { command.descriptor.color }
            val postfix = resolve { command.descriptor.postfix }
            prefix + color + descriptor(user) + postfix
        }

    fun matchingScore(sender: Sender, value: String): Double
}
