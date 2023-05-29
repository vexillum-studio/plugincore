package com.vexillum.plugincore.command.extractor
import com.vexillum.plugincore.managers.language.LanguageAgent

open class StringExtractor<Sender : LanguageAgent>(
    override val descriptor: (LanguageAgent) -> String,
) : ArgumentExtractor<Sender, String> {

    override val extractor = { _: Sender, value: String ->
        value
    }
}
