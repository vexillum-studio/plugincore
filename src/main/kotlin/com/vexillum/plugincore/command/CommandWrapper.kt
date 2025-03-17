package com.vexillum.plugincore.command

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.command.session.Session
import com.vexillum.plugincore.entities.BukkitConsole
import com.vexillum.plugincore.entities.Console.languageState
import com.vexillum.plugincore.extensions.tryCastOrNull
import com.vexillum.plugincore.language.LanguageAgent
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.regex.Pattern
import org.bukkit.command.Command as BukkitCommand

internal class CommandWrapper<C : CommandSender, A : LanguageAgent>(
    pluginCore: PluginCore,
    private val agentSupplier: (C) -> A?,
    private val internalCommand: Command<A>
) : BukkitCommand(
    internalCommand.name,
    internalCommand.description?.let { it(BukkitConsole) } ?: internalCommand.name,
    BukkitConsole.languageState(pluginCore).resolve { command.unknownUsage }(),
    internalCommand.aliases.toList()
),
    Listener {

    private val pattern = Pattern.compile("^${internalCommand.startToken}(?<label>\\w+)\\s?(?<captured>.*)\$")

    private fun applyToSession(
        agent: A,
        block: Session<A>.() -> Session<A>
    ): CommandSession<A> {
        val currentSession = agent.currentCommandSession.tryCastOrNull<Session<A>>()
        val appliedSession = currentSession?.block() ?: Session(agent).block()
        agent.currentCommandSession = appliedSession
        return appliedSession
    }

    @EventHandler
    fun on(event: PlayerCommandPreprocessEvent) {
        val sender = agentFromSender(event.player) ?: return
        val message = event.message
        val matcher = pattern.matcher(message)
        if (!matcher.matches()) {
            return
        }
        val label = matcher.group("label")
        if (!internalCommand.matches(label)) {
            return
        }
        val captured = matcher.group("captured")
        applyToSession(sender) { copy(capturedInput = captured) }
    }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (!internalCommand.matches(label)) return false
        val agent = agentFromSender(sender) ?: return false
        val session = applyToSession(agent) {
            copy(
                command = internalCommand,
                args = args
            )
        }
        try {
            agent.currentCommandSession = session
            internalCommand.execute(session)
            return true
        } catch (e: CommandException) {
            agent.sendMessage(e.languageMessage)
            return false
        } finally {
            agent.currentCommandSession = null
        }
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        val agent = agentFromSender(sender) ?: return mutableListOf()
        try {
            val session = applyToSession(agent) {
                copy(
                    command = internalCommand,
                    args = args
                )
            }
            agent.currentCommandSession = session
            return internalCommand.autocomplete(session)
        } finally {
            agent.currentCommandSession = null
        }
    }

    override fun getAliases(): MutableList<String> =
        internalCommand.aliases.toMutableList()

    override fun getPermission(): String? =
        internalCommand.permission

    private fun agentFromSender(sender: CommandSender): A? =
        sender.tryCastOrNull<C>()?.let { commandSender ->
            agentSupplier(commandSender)
        }
}
