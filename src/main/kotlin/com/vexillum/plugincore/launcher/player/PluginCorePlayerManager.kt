package com.vexillum.plugincore.launcher.player

import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class PluginCorePlayerManager {

    private val players = ConcurrentHashMap<UUID, PluginCorePlayer>()

    fun getOrCreatePluginCorePlayer(uuid: UUID): PluginCorePlayer =
        getOrCreatePluginCorePlayer(Bukkit.getPlayer(uuid)!!)

    fun getOrCreatePluginCorePlayer(player: Player): PluginCorePlayer =
        players.computeIfAbsent(player.uniqueId) { PluginCorePlayer(player) }
}
