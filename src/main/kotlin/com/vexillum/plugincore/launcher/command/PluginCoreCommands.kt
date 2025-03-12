package com.vexillum.plugincore.launcher.command

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.command.argument.EnumArgument
import com.vexillum.plugincore.command.argument.LocationArgument
import com.vexillum.plugincore.command.argument.PlayerArgument
import com.vexillum.plugincore.command.argument.SenderLocationArgument
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message
import org.bukkit.Material

internal data class PluginCoreCommands(
    val reload: PluginCoreReload
) : Language

internal data class PluginCoreReload(
    val start: Message,
    val success: Message
)

@Suppress("LongMethod")
internal fun registerCommands(
    pluginCore: PluginCore
) {
    val managerFactory = pluginCore.managerFactory
    val commandManager = pluginCore.commandManager

    val commandsLanguage = managerFactory.newLanguageManager(
        PluginCoreCommands::class,
        "commands",
        "commands"
    )

    with(commandsLanguage) {
        commandManager.registerPlayerCommand {
            name = "pc-reload"
            permission = "pc.reload"
            addAliases("pcr")
            addUsage { player ->
                player.sendPrefixedMessage { resolve { reload.start } }
                managerFactory.reload()
                player.sendPrefixedMessage { resolve { reload.success } }
            }
        }

        commandManager.registerPlayerCommand {
            name = "warp"
            permission = "player.teleport"
            description {
                "teleports to somewhere"
            }
            addUsage(
                SenderLocationArgument()
            ) { player, location ->
                player.teleport(location)
                with(location) {
                    player.sendPrefixedMessage { message("Teleported to $x, $y, $z") }
                }
            }
            addUsage(
                LocationArgument()
            ) { player, location ->
                player.teleport(location)
                with(location) {
                    player.sendPrefixedMessage { message("Teleported to ${world.name}, $x, $y, $z") }
                }
            }
            addUsage(
                PlayerArgument("player"),
                SenderLocationArgument()
            ) { player, other, location ->
                other.teleport(location)
                with(location) {
                    player.sendPrefixedMessage { message("${other.name} teleported to $x, $y, $z") }
                    other.sendPrefixedMessage { message("You were teleported by ${player.name} to $x, $y, $z") }
                }
            }
            addUsage(
                PlayerArgument("player"),
                LocationArgument()
            ) { player, other, location ->
                other.teleport(location)
                with(location) {
                    player.sendPrefixedMessage { message(("${other.name} was teleported to ${world.name} $x, $y, $z")) }
                }
            }
            addUsage(
                PlayerArgument("from"),
                PlayerArgument("to"),
            ) { player, from, to ->
                from.teleport(to)
                player.sendPrefixedMessage { message("${from.name} was teleported to ${to.name}") }
            }
            addSubCommand {
                name = "material"
                addUsage(EnumArgument({ message("material") }, Material::class)) { player, material ->
                    player.sendPrefixedMessage { message("Your material is ${material.name}") }
                }
            }
        }
    }
}
