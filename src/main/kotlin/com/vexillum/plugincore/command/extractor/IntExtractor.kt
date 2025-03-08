package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class IntExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> LanguageMessage
) : BaseArgumentExtractor<Sender, Int>() {

    override fun matchingScore(sender: Sender, value: String): Double =
        if (value.toIntOrNull() != null) 1.0 else 0.0

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value.toInt()
    }

    override fun defaultDescriptor(user: CommandUser<*>): LanguageMessage =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage =
        user.resolve(mapOf("value" to value)) { command.parsing.integer }

    override fun autocomplete(sender: Sender, value: String) =
        suggestions.map { Suggestion<Sender>(it) }

    companion object {
        private val suggestions = (1..10).map { message(it) }
    }
}
