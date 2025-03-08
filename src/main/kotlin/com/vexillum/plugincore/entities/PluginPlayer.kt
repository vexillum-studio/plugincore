package com.vexillum.plugincore.entities

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.UUID

abstract class PluginPlayer internal constructor(
    private val player: Player
) : LanguageAgent, Player by player {

    constructor(uuid: UUID) :
        this(Bukkit.getPlayer(uuid) ?: error("The player with uuid $uuid is offline"))

    final override val activeLanguage: LocalLanguage?
        get() = LocalLanguage.ofCode(player.locale)

    fun playSound(
        sound: Sound,
        location: Location = this.location,
        pitch: Float = 1F,
        volume: Float = 1F
    ) {
        playSound(location, sound, volume, pitch)
    }

    override fun hashCode(): Int =
        uniqueId.hashCode()

    override fun equals(other: Any?): Boolean =
        uniqueId == (other as? PluginPlayer)?.uniqueId
}

fun Player.pluginPlayer(): PluginPlayer =
    PluginCorePlayer.of(this)
