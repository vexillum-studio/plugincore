package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.NoNextArgumentException
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.CommandSuggestion
import com.vexillum.plugincore.managers.language.LanguageAgent

/**
 * First step in command argument parse process, parses from [String] to the desired type
 */
@Suppress("RethrowCaughtException")
interface ArgumentExtractor<Sender : LanguageAgent, Type : Any> {

    val extractor: (CommandUser<Sender>, String) -> Type

    val descriptor: ((CommandUser<*>) -> String)?
        get() = null

    val errorMessage: ((CommandUser<*>, value: String) -> String)?
        get() = null

    fun descriptor(user: CommandUser<*>): String

    fun errorMessage(user: CommandUser<*>, value: String): String

    fun extract(user: CommandUser<Sender>, value: String): Type =
        try {
            extractor(user, value)
        } catch (e: NoNextArgumentException) {
            throw e
        } catch (e: Exception) {
            throw CommandException(errorMessage(user, value))
        }

    fun autocomplete(sender: Sender, value: String): List<CommandSuggestion<Sender>> = emptyList()

    fun describe(user: CommandUser<*>): String =
        with(user) {
            val color = resolve { command.descriptor.color }
            val prefix = resolve { command.descriptor.prefix }
            val postfix = resolve { command.descriptor.postfix }
            "$color$prefix${descriptor(user)}$postfix"
        }

    fun matchingScore(sender: Sender, value: String): Double
}
