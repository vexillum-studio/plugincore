package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message

@Suppress("MagicNumber")
internal class SubCommandSuggestion<Sender : LanguageAgent>(
    value: Message
) : BaseCommandSuggestion<Sender>(value) {

    override val priority = 3
}
