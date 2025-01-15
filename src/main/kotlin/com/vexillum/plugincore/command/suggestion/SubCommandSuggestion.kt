package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.managers.language.LanguageAgent

@Suppress("MagicNumber")
internal class SubCommandSuggestion<Sender : LanguageAgent>(
    value: String
) : BaseCommandSuggestion<Sender>(value) {

    override val priority = 3
}
