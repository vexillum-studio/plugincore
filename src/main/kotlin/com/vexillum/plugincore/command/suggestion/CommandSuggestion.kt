package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent

interface CommandSuggestion<Sender : LanguageAgent> : Comparable<CommandSuggestion<Sender>> {
    val value: String
    val priority: Int
    val matchable: Boolean
    fun describe(user: CommandUser<*>): String
}
