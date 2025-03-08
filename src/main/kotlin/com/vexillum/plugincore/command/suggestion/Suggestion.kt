package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

@Suppress("MagicNumber")
class Suggestion<Sender : LanguageAgent>(
    value: LanguageMessage
) : BaseCommandSuggestion<Sender>(value) {

    override val priority = 4
}
