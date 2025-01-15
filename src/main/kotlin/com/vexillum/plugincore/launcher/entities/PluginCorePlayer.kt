package com.vexillum.plugincore.launcher.entities

import com.vexillum.plugincore.launcher.player.PluginCorePlayerManager
import com.vexillum.plugincore.managers.language.PluginPlayer
import org.bukkit.entity.Player
import java.util.UUID

internal class PluginCorePlayer(
    player: Player
) : PluginPlayer(player) {

    val persistedLanguage = player.locale

    companion object {
        fun of(uuid: UUID) =
            PluginCorePlayerManager.getOrCreatePluginCorePlayer(uuid)
    }
}
