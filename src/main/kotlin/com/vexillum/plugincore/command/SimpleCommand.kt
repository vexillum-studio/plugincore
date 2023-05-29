package com.vexillum.plugincore.command

import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.managers.language.LanguageAgent

class SimpleCommand<Sender : LanguageAgent>(
    override val name: CommandName,
    override val aliases: Set<CommandName>,
    override val description: ((Sender) -> String)?,
    override val permission: String?,
    override val usages: List<CommandUsage<Sender>>,
    override val subCommands: Set<Command<Sender>>
) : Command<Sender> {

    override fun execute(sender: Sender, args: Array<String>) {
        if (permission?.let { sender.hasPermission(it) } == false) {
            PluginCoreLauncher.instance.withAgent(sender) {
                commandException { command.permissionMessage }
            }
        }

        var lastException: CommandException? = null

        // Check for subcommand matching
        args.firstOrNull()?.let { firstArg ->
            val newArgs = args.copyOfRange(1, args.size)
            subCommands.forEach { subCommand ->
                if (subCommand.matches(firstArg)) {
                    try {
                        println("subcommand: " + subCommand.name)
                        subCommand.execute(sender, newArgs)
                        return
                    } catch (e: CommandException) {
                        lastException = e
                    }
                }
            }
        }

        usages.forEach { usage ->
            try {
                println("usage: " + usage.describe(sender))
                usage.execute(sender, args)
                return
            } catch (e: CommandException) {
                lastException = e
            }
        }
        if (lastException != null) {
            println("threw: " + lastException!!::class.simpleName)
        }
    }

    override fun matches(label: String): Boolean =
        name.equals(label, ignoreCase = true) || aliases.any {
            it.equals(label, ignoreCase = true)
        }

    override fun autocomplete(sender: Sender, value: String): MutableList<String> {
        return mutableListOf()
    }

    override fun hashCode(): Int =
        name.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is Command<*>) return false
        return name == other.name
    }
}
