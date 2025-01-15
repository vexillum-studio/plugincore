package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.extensions.tryCastOrNull
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import org.bukkit.command.Command as BukkitCommand

internal class CommandWrapper<C : CommandSender, A : LanguageAgent>(
    private val agentSupplier: (C) -> A?,
    private val command: Command<A>
) : BukkitCommand(
    command.name,
    command.name,
    command.toString(),
    command.aliases.toList()
),
    Listener {

    private val sessions = ConcurrentHashMap<A, CommandSession>()

    private val pattern = Pattern.compile("^${command.startToken}(?<label>\\w+)\\s?(?<captured>.*)\$")

    private fun session(agent: A) =
        sessions.computeIfAbsent(agent) { CommandSession() }

    @EventHandler
    fun onCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val sender = agentFromSender(event.player) ?: return
        val message = event.message
        val matcher = pattern.matcher(message)
        if (!matcher.matches()) {
            sessions.remove(sender)
        }
        val label = matcher.group("label")
        if (!command.matches(label)) {
            sessions.remove(sender)
        }
        val captured = matcher.group("captured")
        val session = session(sender)
        session.capturedInput = captured
        event.isCancelled = true
    }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        val agent = agentFromSender(sender) ?: return false
        val session = session(agent)
        session.currentArgs = args
        try {
            command.execute(agent, session)
        } catch (e: CommandException) {
            agent.sendMessage(e.message)
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        val agent = agentFromSender(sender) ?: return mutableListOf()
        val session = session(agent)
        session.currentArgs = args
        return command.autocomplete(agent, session)
    }

    override fun getAliases(): MutableList<String> =
        command.aliases.toMutableList()

    override fun getPermission(): String? =
        command.permission

    private fun agentFromSender(sender: CommandSender): A? =
        sender.tryCastOrNull<C>()?.let { commandSender ->
            agentSupplier(commandSender)
        }
}
