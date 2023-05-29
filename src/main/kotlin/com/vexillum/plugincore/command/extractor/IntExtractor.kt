package com.vexillum.plugincore.command.extractor
import com.vexillum.plugincore.managers.language.LanguageAgent

class IntExtractor<Sender : LanguageAgent>(
    override val descriptor: (LanguageAgent) -> String
) : ArgumentExtractor<Sender, Int> {

    override val extractor = { _: Sender, value: String ->
        value.toInt()
    }

    override val errorMessage = { sender: Sender, value: String ->
        sender.defaultCommandMessage(mapOf("value" to value)) { command.parsing.integer }
    }

    override fun autocomplete(sender: Sender, value: String) = suggestions

    companion object {
        private val suggestions = (1..10).map { it.toString() }
    }
}
