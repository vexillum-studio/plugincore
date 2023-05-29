package com.vexillum.plugincore.command

import com.vexillum.plugincore.managers.language.LanguageAgent

typealias CommandName = String

interface Command<Sender : LanguageAgent> {
    val name: CommandName
    val aliases: Set<CommandName>
    val description: ((Sender) -> String)?
    val permission: String?
    val usages: List<CommandUsage<Sender>>
    val subCommands: Set<Command<Sender>>
    fun matches(label: String): Boolean
    fun execute(sender: Sender, args: Array<String>)
    fun autocomplete(sender: Sender, value: String): MutableList<String>

    fun commandException(message: String): Nothing =
        throw CommandException(message)
}
