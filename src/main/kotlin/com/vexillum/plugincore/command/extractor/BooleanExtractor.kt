package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.managers.language.LanguageAgent

open class BooleanExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> String
) : BaseArgumentExtractor<Sender, Boolean>() {

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value.toBoolean()
    }

    override fun defaultDescriptor(user: CommandUser<*>): String =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): String =
        user.resolve(mapOf("value" to value)) { command.parsing.boolean }

    override fun autocomplete(sender: Sender, value: String) =
        possibleValues.map { Suggestion<Sender>(it) }

    companion object {
        private val possibleValues = setOf(true.toString(), false.toString())
    }
}
