package com.vexillum.plugincore.launcher.player

import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal object PluginCorePlayerManager {

    private val players = ConcurrentHashMap<UUID, PluginCorePlayer>()

    fun getOrCreatePluginCorePlayer(uuid: UUID) =
        players.computeIfAbsent(uuid) { PluginCorePlayer(uuid) }
}
