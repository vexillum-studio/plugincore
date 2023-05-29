package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.conversations.Conversable
import org.bukkit.entity.Player
import java.util.UUID

interface LanguageAgent : CommandSender, Conversable {

    val activeLanguage: LocalLanguage?

    val localLanguage get() = activeLanguage ?: LocalLanguage.DEFAULT

    fun <T : Any> sendMessage(
        context: LanguageContext<T>,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) {
        val resolved = context.language(localLanguage).resolve(replacements, block)
        sendMessage(resolved)
    }

    fun <T : Any> commandException(
        languageContext: LanguageContext<T>,
        replacements: Map<String, Any> = emptyMap(),
        sound: Sound? = null,
        block: T.() -> Message
    ): Nothing {
        languageContext.commandException(this, replacements, block)
    }
}

private val consoleSender = Bukkit.getConsoleSender()

object BukkitConsole : LanguageAgent, ConsoleCommandSender by consoleSender {

    override val activeLanguage: LocalLanguage
        get() = ENGLISH // TODO Change to config on plugincore launcher
}

abstract class OfflinePluginPlayer private constructor(
    private val offlinePlayer: OfflinePlayer
) : LanguageAgent, OfflinePlayer by offlinePlayer {

    constructor(uuid: UUID) : this(Bukkit.getOfflinePlayer(uuid))

    override fun getName(): String =
        offlinePlayer.name ?: uniqueId.toString()

    @Transient
    override val activeLanguage: LocalLanguage? =
        PluginCorePlayer.of(uniqueId).language

    override fun hashCode(): Int =
        uniqueId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        return uniqueId == (other as PluginPlayer).uniqueId
    }
}

open class PluginPlayer(
    private val player: Player
) : LanguageAgent, Player by player {

    constructor(uuid: UUID) :
        this(Bukkit.getPlayer(uuid) ?: error("The player with uuid $uuid is offline"))

    final override val activeLanguage: LocalLanguage? =
        PluginCorePlayer.of(uniqueId).language ?: LocalLanguage.ofCode(player.locale)

    override fun hashCode(): Int =
        uniqueId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        return uniqueId == (other as PluginPlayer).uniqueId
    }
}
