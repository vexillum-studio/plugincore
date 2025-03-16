package com.vexillum.plugincore.command

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.entities.Console.commandSession
import com.vexillum.plugincore.extensions.PluginCoreExtensions
import com.vexillum.plugincore.language.LanguageAgent

@Suppress("TooManyFunctions", "LongParameterList")
class CommandBuilder<Sender : LanguageAgent> internal constructor(
    override val pluginCore: PluginCore
) : PluginCoreExtensions {

    var startToken: String = Command.SLASH
    lateinit var name: CommandName
    private var aliases: MutableSet<CommandName> = mutableSetOf()
    private var description: ((LanguageAgent) -> String)? = null
    var permission: String? = null
    private var usages: MutableList<CommandUsage<Sender>> = mutableListOf()
    private var subCommands: MutableSet<SimpleCommand<Sender>> = mutableSetOf()

    fun description(block: (LanguageAgent) -> String) {
        description = block
    }

    fun addAliases(vararg alias: CommandName) {
        aliases.addAll(alias)
    }

    fun addUsage(
        block: (Sender) -> Unit
    ) {
        usages.add(CommandUsage0(block))
    }

    fun <T1 : Any> addUsage(
        arg: Argument<Sender, T1>,
        block: (Sender, T1) -> Unit
    ) {
        usages.add(CommandUsage1(arg, block))
    }

    fun <T1 : Any, T2 : Any> addUsage(
        arg1: Argument<Sender, T1>,
        arg2: Argument<Sender, T2>,
        block: (Sender, T1, T2) -> Unit
    ) {
        usages.add(CommandUsage2(arg1, arg2, block))
    }

    fun <T1 : Any, T2 : Any, T3 : Any> addUsage(
        arg1: Argument<Sender, T1>,
        arg2: Argument<Sender, T2>,
        arg3: Argument<Sender, T3>,
        block: (Sender, T1, T2, T3) -> Unit
    ) {
        usages.add(CommandUsage3(arg1, arg2, arg3, block))
    }

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> addUsage(
        arg1: Argument<Sender, T1>,
        arg2: Argument<Sender, T2>,
        arg3: Argument<Sender, T3>,
        arg4: Argument<Sender, T4>,
        block: (Sender, T1, T2, T3, T4) -> Unit
    ) {
        usages.add(CommandUsage4(arg1, arg2, arg3, arg4, block))
    }

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> usage(
        arg1: Argument<Sender, T1>,
        arg2: Argument<Sender, T2>,
        arg3: Argument<Sender, T3>,
        arg4: Argument<Sender, T4>,
        arg5: Argument<Sender, T5>,
        block: (Sender, T1, T2, T3, T4, T5) -> Unit
    ) {
        usages.add(CommandUsage5(arg1, arg2, arg3, arg4, arg5, block))
    }

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> usage(
        arg1: Argument<Sender, T1>,
        arg2: Argument<Sender, T2>,
        arg3: Argument<Sender, T3>,
        arg4: Argument<Sender, T4>,
        arg5: Argument<Sender, T5>,
        arg6: Argument<Sender, T6>,
        block: (Sender, T1, T2, T3, T4, T5, T6) -> Unit
    ) {
        usages.add(CommandUsage6(arg1, arg2, arg3, arg4, arg5, arg6, block))
    }

    fun addSubCommand(block: CommandBuilder<Sender>.() -> Unit) {
        val command = CommandBuilder<Sender>(pluginCore).also(block).build()
        subCommands.add(command)
    }

    fun addHelpCommand(
        label: String = Command.DEFAULT_HELP_LABEL
    ) {
        addSubCommand {
            name = label
            aliases = mutableSetOf("?")
            addUsage { sender ->
                val session = sender.commandSession()
                    ?: error("No current session")
                val sessionCommand = session.command
                    ?: error("No current command")
                sender.sendPrefixedMessage {
                    sender.defaultState { resolve { command.helpMessage }.replace("label", sessionCommand.name) } +
                        sessionCommand.usagesMessage(session)
                }
            }
        }
    }

    internal fun build(): SimpleCommand<Sender> {
        require(::name.isInitialized) { "The property name must be defined" }
        return SimpleCommand(
            pluginCore,
            startToken,
            name,
            aliases,
            description,
            permission,
            usages,
            subCommands
        )
    }
}
