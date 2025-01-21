package com.vexillum.plugincore.managers.command

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.command.CommandBuilder
import com.vexillum.plugincore.command.CommandName
import com.vexillum.plugincore.command.CommandWrapper
import com.vexillum.plugincore.entities.BukkitConsole
import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.entities.pluginPlayer
import com.vexillum.plugincore.extensions.registerEvents
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.util.fieldValue
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class CommandManager internal constructor(val pluginCore: PluginCore) {

    private val registeredCommands = mutableMapOf<String, CommandWrapper<*, *>>()

    private val commandMap by lazy {
        Bukkit.getServer().fieldValue<CommandMap>(COMMAND_MAP_FIELD)
    }

    fun unregisterAll() {
        registeredCommands.values.forEach { it.unregister(commandMap) }
        registeredCommands.clear()
    }

    fun unregisterCommand(name: CommandName) {
        val command = commandMap.getCommand(name)
        command?.unregister(commandMap)
        registeredCommands.remove(name)
    }

    fun registerConsoleCommand(
        block: CommandBuilder<BukkitConsole>.() -> Unit
    ) {
        registerCommand<ConsoleCommandSender, BukkitConsole>(block) { BukkitConsole }
    }

    fun registerPlayerCommand(
        block: CommandBuilder<PluginPlayer>.() -> Unit
    ) {
        registerCommand<Player, PluginPlayer>(block) { it.pluginPlayer() }
    }

    fun <C : CommandSender, Sender : LanguageAgent> registerCommand(
        block: CommandBuilder<Sender>.() -> Unit,
        agentSupplier: (C) -> Sender?
    ) {
        val command = CommandBuilder<Sender>(pluginCore).also(block).build()
        val wrapper = CommandWrapper(agentSupplier, command)
        pluginCore.plugin.registerEvents(wrapper)
        with(wrapper) {
            unregisterCommand(name)
            commandMap.register(name, this)
            registeredCommands[name] = this
        }
    }

    companion object {
        private const val COMMAND_MAP_FIELD = "commandMap"
    }
}
