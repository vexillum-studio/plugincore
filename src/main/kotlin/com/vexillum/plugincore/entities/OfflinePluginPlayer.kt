package com.vexillum.plugincore.entities

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LocalLanguage
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.UUID

abstract class OfflinePluginPlayer private constructor(
    private val offlinePlayer: OfflinePlayer
) : LanguageAgent, OfflinePlayer by offlinePlayer {

    constructor(uuid: UUID) : this(Bukkit.getOfflinePlayer(uuid))

    override fun getName(): String =
        offlinePlayer.name ?: uniqueId.toString()

    @Transient
    override val activeLanguage: LocalLanguage? =
        null // PluginCorePlayer.of(uniqueId).persistedLanguage.let { LocalLanguage.ofCode(it) }

    override var currentCommandSession: CommandSession<*>? = null

    override fun hashCode(): Int =
        uniqueId.hashCode()

    override fun equals(other: Any?): Boolean =
        uniqueId == (other as? PluginPlayer)?.uniqueId
}
