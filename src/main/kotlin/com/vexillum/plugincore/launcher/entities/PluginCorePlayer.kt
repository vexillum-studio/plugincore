package com.vexillum.plugincore.launcher.entities

import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import org.bukkit.entity.Player
import java.util.UUID

internal class PluginCorePlayer(
    player: Player
) : PluginPlayer(player) {

    companion object {
        fun of(uuid: UUID) =
            pluginCoreInstance.playerManager.getOrCreatePluginCorePlayer(uuid)

        fun of(player: Player) =
            pluginCoreInstance.playerManager.getOrCreatePluginCorePlayer(player)
    }
}
