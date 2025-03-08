package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

@Suppress("MagicNumber")
internal class SubCommandSuggestion<Sender : LanguageAgent>(
    value: LanguageMessage
) : BaseCommandSuggestion<Sender>(value) {

    override val priority = 3
}
