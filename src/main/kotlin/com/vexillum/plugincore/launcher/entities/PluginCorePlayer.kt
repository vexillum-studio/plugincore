package com.vexillum.plugincore.launcher.entities

import com.vexillum.plugincore.launcher.player.PluginCorePlayerManager
import com.vexillum.plugincore.managers.language.LocalLanguage
import java.util.UUID

internal class PluginCorePlayer(
    val uuid: UUID,
    val language: LocalLanguage? = null
) {

    companion object {
        fun of(uuid: UUID) =
            PluginCorePlayerManager.getOrCreatePluginCorePlayer(uuid)
    }
}
