package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message

@Suppress("MagicNumber")
class Suggestion<Sender : LanguageAgent>(
    value: Message
) : BaseCommandSuggestion<Sender>(value) {

    override val priority = 4
}
