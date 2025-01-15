package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.managers.language.LanguageAgent

typealias CommandName = String

interface Command<Sender : LanguageAgent> {
    val startToken: String
    val name: CommandName
    val aliases: Set<CommandName>
    val description: ((Sender) -> String)?
    val permission: String?
    val usages: List<CommandUsage<Sender>>
    val subCommands: Set<Command<Sender>>
    fun matches(label: String): Boolean
    fun execute(sender: Sender, session: CommandSession)
    fun autocomplete(sender: Sender, session: CommandSession): MutableList<String>

    fun commandException(message: String): Nothing =
        throw CommandException(message)

    companion object {
        const val SLASH = "/"
        const val DOUBLE_SLASH = "//"
    }
}
