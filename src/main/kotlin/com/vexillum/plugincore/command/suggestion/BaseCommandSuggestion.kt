package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.managers.language.LanguageAgent

abstract class BaseCommandSuggestion<Sender : LanguageAgent>(
    override val value: String
) : CommandSuggestion<Sender> {

    override val matchable = true

    override fun compareTo(other: CommandSuggestion<Sender>): Int {
        val priorityComparison = priority.compareTo(other.priority)
        if (priorityComparison == 0) {
            return value.compareTo(other.value)
        }
        return priorityComparison
    }

    override fun describe(sender: Sender): String =
        value

    override fun toString() = value

    override fun equals(other: Any?) =
        (other as? CommandSuggestion<*>)?.value == value

    override fun hashCode() =
        value.hashCode()
}
