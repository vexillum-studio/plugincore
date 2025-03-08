package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.session.ConsoleUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

abstract class BaseCommandSuggestion<Sender : LanguageAgent>(
    val message: LanguageMessage
) : CommandSuggestion<Sender> {

    override val value = message.resolved()

    override val matchable = true

    override fun compareTo(other: CommandSuggestion<Sender>): Int {
        val priorityComparison = priority.compareTo(other.priority)
        return priorityComparison
    }

    override fun describe(user: CommandUser<*>): LanguageMessage =
        message

    override fun toString() = describe(ConsoleUser).stripped()

    override fun equals(other: Any?) =
        (other as? CommandSuggestion<*>)?.value == value

    override fun hashCode() =
        value.hashCode()
}
