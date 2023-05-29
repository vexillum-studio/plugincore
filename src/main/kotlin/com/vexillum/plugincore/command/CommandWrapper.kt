package com.vexillum.plugincore.command

import com.vexillum.plugincore.extensions.tryCastOrNull
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.command.CommandSender
import org.bukkit.command.Command as BukkitCommand

internal class CommandWrapper<C : CommandSender, Sender : LanguageAgent>(
    private val agentSupplier: (C) -> Sender?,
    private val command: Command<Sender>
) : BukkitCommand(
    command.name,
    "NOPE DESC",
    "NOPE USAGE",
    command.aliases.toList()
) {

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        val languageAgent = agentFromSender(sender) ?: return false
        try {
            command.execute(languageAgent, args)
        } catch (e: CommandException) {
            languageAgent.sendMessage(e.message)
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        val languageAgent = agentFromSender(sender) ?: return mutableListOf()
        return command.autocomplete(languageAgent, args.last())
    }

    override fun getAliases(): MutableList<String> =
        command.aliases.toMutableList()

    override fun getPermission(): String? =
        command.permission

    private fun agentFromSender(sender: CommandSender): Sender? =
        sender.tryCastOrNull<C>()?.let { commandSender ->
            agentSupplier(commandSender)
        }
}
