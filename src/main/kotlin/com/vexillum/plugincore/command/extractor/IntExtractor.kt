package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.managers.language.LanguageAgent

open class IntExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> String
) : BaseArgumentExtractor<Sender, Int>() {

    override fun matchingScore(sender: Sender, value: String): Double =
        if (value.toIntOrNull() != null) 1.0 else -1.0

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value.toInt()
    }

    override fun defaultDescriptor(user: CommandUser<*>): String =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): String =
        user.resolve(mapOf("value" to value)) { command.parsing.integer }

    override fun autocomplete(sender: Sender, value: String) =
        suggestions.map { Suggestion<Sender>(it) }

    companion object {
        private val suggestions = (1..10).map { it.toString() }
    }
}
