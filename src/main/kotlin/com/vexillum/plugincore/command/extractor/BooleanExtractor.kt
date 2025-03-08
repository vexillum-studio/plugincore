package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class BooleanExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> LanguageMessage
) : BaseArgumentExtractor<Sender, Boolean>() {

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value.toBoolean()
    }

    override fun defaultDescriptor(user: CommandUser<*>): LanguageMessage =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage =
        user.resolve(mapOf("value" to value)) { command.parsing.boolean }

    override fun autocomplete(sender: Sender, value: String) =
        possibleValues.map { Suggestion<Sender>(it) }

    companion object {
        private val possibleValues = listOf(true, false).map { message(it.toString()) }
    }
}
