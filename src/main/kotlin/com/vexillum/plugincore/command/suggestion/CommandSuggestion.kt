package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

interface CommandSuggestion<Sender : LanguageAgent> : Comparable<CommandSuggestion<Sender>> {
    val value: String
    val priority: Int
    val matchable: Boolean
    fun describe(user: CommandUser<*>): LanguageMessage
}
