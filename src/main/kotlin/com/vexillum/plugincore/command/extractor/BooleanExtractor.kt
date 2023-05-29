package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.managers.language.LanguageAgent

class BooleanExtractor<Sender : LanguageAgent>(
    override val descriptor: (LanguageAgent) -> String
) : ArgumentExtractor<Sender, Boolean> {

    override val extractor = { _: Sender, value: String ->
        value.toBoolean()
    }

    override val errorMessage = { sender: Sender, value: String ->
        sender.defaultCommandMessage(mapOf("value" to value)) { command.parsing.boolean }
    }

    override fun autocomplete(sender: Sender, value: String): List<String> =
        possibleValues

    companion object {
        private val possibleValues = listOf(true.toString(), false.toString())
    }
}
