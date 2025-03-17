package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.context.DefaultState
import com.vexillum.plugincore.language.message.Message

typealias CommandName = String

interface Command<Sender : LanguageAgent> {
    val startToken: String get() = SLASH
    val name: CommandName
    val aliases: Set<CommandName>
    val description: ((LanguageAgent) -> String)?
    val permission: String?
    val usages: List<CommandUsage<Sender>>
    val subCommands: Set<Command<Sender>>
    fun matches(label: String): Boolean
    fun execute(session: CommandSession<Sender>)
    fun autocomplete(session: CommandSession<Sender>): MutableList<String>
    fun <A : LanguageAgent> A.commandException(
        block: DefaultState<A>.() -> Message
    ): Nothing
    fun <A : LanguageAgent> A.commandMessage(
        block: DefaultState<A>.() -> Message
    )
    fun usagesMessage(
        languageState: DefaultState<Sender>
    ): Message

    companion object {
        const val SLASH = "/"
        const val DOUBLE_SLASH = "$SLASH$SLASH"
        const val DEFAULT_HELP_LABEL = "help"
        const val DEFAULT_HELP_ALIAS = "?"
    }
}
