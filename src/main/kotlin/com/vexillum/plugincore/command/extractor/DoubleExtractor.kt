package com.vexillum.plugincore.command.extractor
import com.vexillum.plugincore.managers.language.LanguageAgent

class DoubleExtractor<Sender : LanguageAgent>(
    override val descriptor: (LanguageAgent) -> String
) : ArgumentExtractor<Sender, Double> {

    override val extractor = { _: Sender, value: String ->
        value.toDouble()
    }

    override val errorMessage = { sender: Sender, value: String ->
        sender.defaultCommandMessage(mapOf("value" to value)) { command.parsing.double }
    }
}
